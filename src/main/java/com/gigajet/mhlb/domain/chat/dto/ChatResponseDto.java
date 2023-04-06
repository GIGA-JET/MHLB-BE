package com.gigajet.mhlb.domain.chat.dto;

import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponseDto {
    @Getter
    public static class GetUuid {
        private String uuid;

        public GetUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Inbox {
        private String uuid;
        private String userImage;
        private String userName;
        private LocalDateTime lastChat;
        private String message;
        private Long userId;
        private Long unreadMessages;
        private Integer color;

        public void inbox(ChatRoom chatRoom, User user, Integer status) {
            this.uuid = chatRoom.getInBoxId();
            this.userId = user.getId();
            this.userImage = user.getImage();
            this.userName = user.getUsername();
            this.lastChat = chatRoom.getLastChat();
            this.message = chatRoom.getLastMessage();
            this.color = status;
        }

        public void unreadMessage(Long unreadMessages) {
            this.unreadMessages = unreadMessages;
        }
    }

    @Getter
    public static class Chat {
        private Long messageId;
        private Long userId;
        private String message;
        private LocalDateTime createdAt;
        private ChatRequestDto.MessageType type;

        public Chat(com.gigajet.mhlb.domain.chat.entity.Chat chat) {
            this.messageId = chat.getMessageId();
            this.message = chat.getMessage();
            this.userId = chat.getSenderId();
            this.createdAt = chat.getCreatedAt();
        }

        public Chat(ChatRequestDto.Chat chat){
            this.messageId = chat.getMessageId();
            this.message = chat.getMessage();
            this.userId = chat.getSenderId();
        }
    }
}
