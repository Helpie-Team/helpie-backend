package com.helpie.backend.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 웹소켓 에러 메시지 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Schema(description = "WebSocket 에러 메시지 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketErrorMessage {
    
    @Schema(description = "메시지 타입", example = "ERROR", defaultValue = "ERROR")
    private String type = "ERROR";
    
    @Schema(description = "에러 코드", example = "WS_ACCESS_DENIED")
    private String errorCode;
    
    @Schema(description = "에러 메시지", example = "접근이 거부되었습니다")
    private String message;
    
    @Schema(description = "에러 상세 정보", example = "인증되지 않은 사용자입니다")
    private String details;
    
    @Schema(description = "에러 발생 시간", example = "2025-10-25T14:30:00")
    private LocalDateTime timestamp;
    
    public static WebSocketErrorMessage of(String errorCode, String message, String details) {
        return new WebSocketErrorMessage(
            "ERROR",
            errorCode,
            message,
            details,
            LocalDateTime.now()
        );
    }
    
    public static WebSocketErrorMessage of(String errorCode, String message) {
        return of(errorCode, message, null);
    }
}