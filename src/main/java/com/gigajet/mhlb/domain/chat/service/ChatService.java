package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.status.repository.StatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    /*
        1. 챗 보내기 -> DB 저장 -> 브로커를 통한 pub 까지 시간이 꽤 오래 걸림
        2. 채팅을 보낼 떄 트랜잭션의 독립성이 지켜지지 않아 N+1 문제 발생

        TODO : 1번 어디서 오래 걸리는지 확인 후 방법 생각해보기
               2번 효율적인 MongoDB Rock 방법 생각해보기
     */

    //이전 채팅목록 불러오기
    @Transactional
    public List<ChatResponseDto.Chatting> getChat(User user, Long workspaceId, Long opponentsId, Pageable pageable) {
        User opponents = userRepository.findById(opponentsId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        Workspace workspace = validateWorkspace(workspaceId);

        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUserAndWorkspace(opponents, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));


        List<ChatResponseDto.Chatting> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);
        if (chatRoom == null) {
            return new ArrayList<>();
        }

        Slice<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId(), pageable);
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chatting(chat));
        }

        chatList.sort(Comparator.comparing(ChatResponseDto.Chatting::getCreatedAt));

        return chatList;
    }

    //uuid 생성 또는 조회
    @Transactional
    public ChatResponseDto.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }

        User opponents = userRepository.findById(opponentsId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        Workspace workspace = validateWorkspace(workspaceId);

        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(opponents, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<UserAndMessage> userIdList = new ArrayList<>();
        userIdList.add(new UserAndMessage(user.getId()));
        userIdList.add(new UserAndMessage(opponentsId));

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);

        if (chatRoom == null) {
            String inboxId = String.valueOf(UUID.randomUUID());

            ChatRoom inbox = ChatRoom.builder()
                    .inBoxId(inboxId)
                    .userAndMessages(userIdList)
                    .userSet(userIdSet)
                    .workspaceId(workspaceId)
                    .build();
            chatRoomRepository.save(inbox);
            return new ChatResponseDto.GetUuid(inbox.getInBoxId());
        }
        return new ChatResponseDto.GetUuid(chatRoom.getInBoxId());
    }

    //인박스 불러오기
    @Transactional
    public List<ChatResponseDto.Inbox> getInbox(User user, Long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);

        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Inbox> response = new ArrayList<>();
        List<ChatRoom> list = chatRoomRepository.findByWorkspaceIdAndUserSetInOrderByLastChatDesc(workspaceId, user.getId());

        for (ChatRoom chatRoom : list) {
            ChatResponseDto.Inbox inbox = new ChatResponseDto.Inbox();
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {//리스트대로 돌림
                if (user.getId() == userAndMessage.getUserId()) {
                    inbox.unreadMessage(userAndMessage.getUnread());
                    continue;
                }
                Optional<User> opponents = userRepository.findById(userAndMessage.getUserId());
                //퇴사한 유저인지 확인하여 퇴사한 경우 채팅 보낼 수 없도록 기능 수정 필요
                inbox.inbox(chatRoom, opponents.get());
            }
            response.add(inbox);
        }
        return response;
    }

    private Workspace validateWorkspace(Long id) {
        return workspaceRepository.findByIdAndIsShowTrue(id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }
}