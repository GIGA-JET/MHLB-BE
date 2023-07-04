package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import com.gigajet.mhlb.domain.alarm.Repository.AlarmRepository;
import com.gigajet.mhlb.domain.alarm.dto.ChatAlarmResponseDto;
import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.MessageId;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.chat.repository.MessageIdRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageIdRepository messageIdRepository;
    private final AlarmRepository alarmRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    //메시지 보내기
    @Transactional
    public Long getMessageId() {
        MessageId messageId = messageIdRepository.findTopByKey(1);
        if (messageId == null) {
            messageId = new MessageId(1L);
        } else {
            messageId.addMessageId();
        }
        messageIdRepository.save(messageId);
        return messageId.getMessageId();
    }

    @Transactional
    public void sendMsg(ChatRequestDto.Chat message, StompHeaderAccessor accessor, Long messageId) {
        Long senderId = Long.valueOf(accessor.getFirstNativeHeader("userId"));
        Long workspaceId = message.getWorkspaceId();

        Chat chat = Chat.builder()
                .senderId(senderId)
                .inBoxId(message.getUuid())
                .workspaceId(workspaceId)
                .message(message.getMessage())
                .messageId(messageId)
                .build();

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());

        Map<Object, Object> room = redisTemplate.opsForHash().entries("/sub/inbox/" + message.getUuid());
        if (room.size() == 1) { //방에 혼자일 경우 상대의 안읽은 메시지를 +1
            Long receiverId = null;
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
                if (Objects.equals(senderId, userAndMessage.getUserId())) {
                    continue;
                }
                receiverId = userAndMessage.getUserId();
                userAndMessage.addUnread();
            }
            checkUnreadMessage(senderId, receiverId, workspaceId, message);
        }
        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);

        chatRepository.save(chat);

        redisTemplate.convertAndSend("chatMessageChannel", new ChatResponseDto.Convert(chat));
    }

    /**
     * disconnect시 endpoint의 값을 받아올 수 없으므로 sessionId : endpoint의 데이터를 저장해둠
     */
    @Transactional
    public void checkRoom(StompHeaderAccessor accessor) {
        String endpoint = accessor.getDestination();
        Long userId = Long.valueOf(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));

        Map<Object, Object> room = redisTemplate.opsForHash().entries(endpoint);
        String sessionId = accessor.getSessionId();

        if (room.size() == 0) {
            Map<String, Long> userSessionInfo = new HashMap<>();

            userSessionInfo.put(sessionId, userId);

            redisTemplate.opsForHash().putAll(endpoint, userSessionInfo);
            redisTemplate.opsForValue().set(sessionId, endpoint);
        } else {
            Map<String, Long> updatedData = new HashMap<>();
            for (Map.Entry<Object, Object> entry : room.entrySet()) {
                String key = (String) entry.getKey();
                Long value = (Long) entry.getValue();
                updatedData.put(key, value);
            }

            updatedData.put(sessionId, userId);
            redisTemplate.opsForHash().putAll(endpoint, updatedData);

            redisTemplate.opsForValue().set(sessionId, endpoint);
        }
    }

    /*
        메세지 읽음 처리 메서드
        기존에 websocket에 session이 연결될 때 사용하던 메서드였지만
        Connect를 한번만, Subscribe을 여러번하기로 변경해서 subscribe event때 실행하는 메서드로 변경
     */
    @Transactional
    public void readMessages(StompHeaderAccessor accessor) {
        long userId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));

        String destination = accessor.getDestination();
        String uuid = destination.substring(destination.lastIndexOf("/") + 1);

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(uuid);

        if (chatRoom == null) {
            // 예외 처리 해야함
            return;
        }

        Optional<Alarm> optionalAlarm = alarmRepository.findByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(userId, chatRoom.getWorkspaceId(), uuid, true);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            alarm.toggleUnreadMessage();
            checkReadMessage(userId, chatRoom.getWorkspaceId(), uuid);
        }

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (userId != userAndMessage.getUserId()) {
                continue;
            }

            userAndMessage.resetUnread();
        }

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void exitRoom(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String endpoint = (String) redisTemplate.opsForValue().get(sessionId);
        if (endpoint == null) {
        }

        Map<Object, Object> room = redisTemplate.opsForHash().entries(endpoint);

        if (room.size() == 1) { //disconnect시 마지막 인원일 경우 바로 삭제
            redisTemplate.delete(sessionId);
            redisTemplate.delete(endpoint);
        } else {
            redisTemplate.delete(sessionId);
            redisTemplate.opsForHash().delete(endpoint, sessionId);
        }
    }

    public void checkUnreadMessage(Long senderId, Long receiverId, Long workspaceId, ChatRequestDto.Chat message) {
        User sender = userRepository.findById(senderId).orElseThrow();

        Optional<Alarm> optionalAlarm = alarmRepository.findByUserIdAndWorkspaceIdAndUuid(receiverId, workspaceId, message.getUuid());
        if (optionalAlarm.isPresent() && !optionalAlarm.get().getUnreadMessage()) {
            optionalAlarm.get().toggleUnreadMessage();
        } else if (optionalAlarm.isEmpty()) {
            // 메시지를 받은 사용자
            User receiver = userRepository.findById(receiverId).orElseThrow();
            Alarm alarm = alarmRepository.save(Alarm.builder()
                    .unreadMessage(true)
                    .type(AlarmTypeEnum.CHAT)
                    .uuid(message.getUuid())
                    .workspaceId(workspaceId)
                    .user(receiver)
                    .build());
        }

        ChatAlarmResponseDto.NewMessageAlarm newMessageAlarm = new ChatAlarmResponseDto.NewMessageAlarm(true, workspaceId, message, senderId, sender.getUsername(), sender.getImage());
        redisTemplate.convertAndSend("chatAlarmMessageChannel", new ChatAlarmResponseDto.ConvertChatAlarm<>(newMessageAlarm, receiverId));
    }

    private void checkReadMessage(Long userId, Long workspaceId, String uuid) {
        List<Alarm> alarmList = alarmRepository.findAllByUserIdAndWorkspaceIdAndUnreadMessage(userId, workspaceId, true);

        if (alarmList.size() == 0) {
            redisTemplate.convertAndSend("chatAlarmMessageChannel", new ChatAlarmResponseDto.ConvertChatAlarm<>(new ChatAlarmResponseDto.ReadAllMessageAlarm(false, workspaceId, uuid), userId));
        } else {
            redisTemplate.convertAndSend("chatAlarmMessageChannel", new ChatAlarmResponseDto.ConvertChatAlarm<>(new ChatAlarmResponseDto.ReadAllMessageAlarm(true, workspaceId, uuid), userId));
        }
    }
}
