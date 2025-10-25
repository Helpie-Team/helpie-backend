package com.helpie.backend.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 웹소켓 채팅 메시지 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Schema(description = "WebSocket 채팅 메시지 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatWebSocketMessage {
    
    @Schema(description = "메시지 타입", example = "CHAT")
    private WebSocketMessageType type;
    
    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;
    
    @Schema(description = "발신자 ID", example = "123")
    private Long senderId;
    
    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;
    
    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
    
    @Schema(description = "전송 시간", example = "2025-10-25T14:30:00")
    private LocalDateTime timestamp;
    
    // 채팅 메시지용 생성자
    public static ChatWebSocketMessage createChatMessage(Long chatRoomId, Long senderId, String senderName, String content) {
        return new ChatWebSocketMessage(
            WebSocketMessageType.CHAT,
            chatRoomId,
            senderId,
            senderName,
            content,
            LocalDateTime.now()
        );
    }
    
    // 시스템 메시지용 생성자
    public static ChatWebSocketMessage createSystemMessage(Long chatRoomId, String content) {
        return new ChatWebSocketMessage(
            WebSocketMessageType.SYSTEM,
            chatRoomId,
            null,
            "시스템",
            content,
            LocalDateTime.now()
        );
    }
    
    // 입장 메시지용 생성자
    public static ChatWebSocketMessage createJoinMessage(Long chatRoomId, Long userId, String userName) {
        return new ChatWebSocketMessage(
            WebSocketMessageType.JOIN,
            chatRoomId,
            userId,
            userName,
            userName + "님이 참가하셨습니다.",
            LocalDateTime.now()
        );
    }
    
    // 퇴장 메시지용 생성자
    public static ChatWebSocketMessage createLeaveMessage(Long chatRoomId, Long userId, String userName) {
        return new ChatWebSocketMessage(
            WebSocketMessageType.LEAVE,
            chatRoomId,
            userId,
            userName,
            userName + "님이 소모임을 나갔습니다.",
            LocalDateTime.now()
        );
    }
}