package com.helpie.backend.dto.chatroom;

import com.helpie.backend.domain.chatroom.ChatMessage;
import com.helpie.backend.domain.chatroom.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private LocalDateTime sentAt;
    
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            chatMessage.getChatRoom().getId(),
            chatMessage.getSenderId(),
            chatMessage.getSenderName(),
            chatMessage.getContent(),
            chatMessage.getMessageType(),
            chatMessage.getSentAt()
        );
    }
}