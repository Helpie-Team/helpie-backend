package com.helpie.backend.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 웹소켓 메시지 타입
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Schema(description = "WebSocket 메시지 타입")
public enum WebSocketMessageType {
    
    @Schema(description = "일반 채팅 메시지")
    CHAT,
    
    @Schema(description = "채팅방 입장 알림")
    JOIN,
    
    @Schema(description = "채팅방 퇴장 알림")
    LEAVE,
    
    @Schema(description = "시스템 메시지 (연결 해제, 알림 등)")
    SYSTEM
}