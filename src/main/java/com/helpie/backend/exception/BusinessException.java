package com.helpie.backend.exception;

/**
 * 비즈니스 로직 처리 중 발생하는 최상위 예외
 * 모든 도메인별 비즈니스 예외의 부모 클래스
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public abstract class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    protected BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    protected BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}