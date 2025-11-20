package com.helpie.backend.dto.chatroom;

import com.helpie.backend.domain.chatroom.ChatMessage;
import com.helpie.backend.domain.chatroom.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "채팅 메시지 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    @Schema(description = "메시지 ID", example = "1")
    private Long id;
    
    @Schema(description = "채팅방 ID", example = "24")
    private Long chatRoomId;
    
    @Schema(description = "발신자 ID", example = "123")
    private Long senderId;
    
    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;
    
    @Schema(description = "발신자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String senderProfileImage;
    
    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
    
    @Schema(description = "메시지 타입", example = "USER")
    private MessageType messageType;
    
    @Schema(description = "전송 시간", example = "2025-11-20T14:30:28.123456")
    private LocalDateTime sentAt;
    
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            chatMessage.getChatRoom().getId(),
            chatMessage.getSenderId(),
            chatMessage.getSenderName(),
            null, // 프로필 이미지는 서비스에서 별도로 설정
            chatMessage.getContent(),
            chatMessage.getMessageType(),
            chatMessage.getSentAt()
        );
    }
    
    public static ChatMessageResponse from(ChatMessage chatMessage, String senderProfileImage) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            chatMessage.getChatRoom().getId(),
            chatMessage.getSenderId(),
            chatMessage.getSenderName(),
            senderProfileImage,
            chatMessage.getContent(),
            chatMessage.getMessageType(),
            chatMessage.getSentAt()
        );
    }
}