package com.helpie.backend.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 웹소켓 채팅 메시지 요청 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Schema(description = "WebSocket 채팅 메시지 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    
    @Schema(description = "채팅방 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long chatRoomId;
    
    @Schema(description = "발신자 ID (JWT 토큰에서 자동 추출)", example = "123", accessMode = Schema.AccessMode.READ_ONLY)
    private Long senderId;
    
    @Schema(description = "발신자 이름 (JWT 토큰에서 자동 추출)", example = "홍길동", accessMode = Schema.AccessMode.READ_ONLY)
    private String senderName;
    
    @Schema(description = "메시지 내용", example = "안녕하세요!", required = true, maxLength = 1000)
    private String content;
}