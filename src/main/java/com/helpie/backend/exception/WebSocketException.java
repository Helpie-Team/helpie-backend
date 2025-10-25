package com.helpie.backend.exception;

/**
 * 웹소켓 관련 예외 클래스
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public class WebSocketException extends RuntimeException {
    
    private final String errorCode;
    
    public WebSocketException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public WebSocketException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // 자주 사용되는 예외들을 static 메서드로 제공
    public static WebSocketException invalidMessage(String details) {
        return new WebSocketException("WS_INVALID_MESSAGE", "유효하지 않은 메시지 형식입니다: " + details);
    }
    
    public static WebSocketException accessDenied(String reason) {
        return new WebSocketException("WS_ACCESS_DENIED", "접근이 거부되었습니다: " + reason);
    }
    
    public static WebSocketException chatRoomNotFound(Long chatRoomId) {
        return new WebSocketException("WS_CHATROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다: " + chatRoomId);
    }
    
    public static WebSocketException connectionError(String details) {
        return new WebSocketException("WS_CONNECTION_ERROR", "연결 오류가 발생했습니다: " + details);
    }
}