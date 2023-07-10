package com.gigajet.mhlb.domain.alarm.dto;

import com.gigajet.mhlb.domain.alarm.entity.Alarm;
import com.gigajet.mhlb.domain.alarm.entity.AlarmTypeEnum;
import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChatAlarmResponseDto {

    @Getter
    @NoArgsConstructor
    public static class NewMessageAlarm {
        private Boolean unreadMessage;
        private Long workspaceId;
        private String uuid;
        private Long senderId;
        private String senderName;
        private String senderImage;
        private String lastMessage;
        private String lastChat;

        public NewMessageAlarm(Boolean unreadMessage, Long workspaceId, ChatRequestDto.Chat message, Long senderId, String senderName, String senderImage) {
            this.unreadMessage = unreadMessage;
            this.workspaceId = workspaceId;
            this.uuid = message.getUuid();
            this.senderId = senderId;
            this.senderName = senderName;
            this.senderImage = senderImage;
            this.lastMessage = message.getMessage();
            this.lastChat = LocalDateTime.now().toString();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReadAllMessageAlarm {
        private Boolean unreadMessage;
        private Long workspaceId;
        private String uuid;

        public ReadAllMessageAlarm(Boolean unreadMessage, Long workspaceId, String uuid) {
            this.unreadMessage = unreadMessage;
            this.workspaceId = workspaceId;
            this.uuid = uuid;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ConvertChatAlarm<T> {
        private T t;
        private Long receiverId;

        public ConvertChatAlarm(T t, Long receiverId) {
            this.t = t;
            this.receiverId = receiverId;
        }
    }

    @Getter
    public static class InviteAlarm {
        private final Long workspaceId;
        private final AlarmTypeEnum type;

        public InviteAlarm(Alarm alarm) {
            this.workspaceId = alarm.getWorkspaceId();
            this.type = alarm.getType();
        }
    }
}
