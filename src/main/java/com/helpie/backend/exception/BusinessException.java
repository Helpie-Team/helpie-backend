package com.helpie.backend.exception;

import java.util.Collections;
import java.util.Map;

/**
 * 비즈니스 로직 처리 중 발생하는 최상위 예외
 * 모든 도메인별 비즈니스 예외의 부모 클래스
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public abstract class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Map<String, Object> data;
    
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = Collections.emptyMap();
    }
    
    protected BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = Collections.emptyMap();
    }
    
    protected BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = Collections.emptyMap();
    }

    protected BusinessException(ErrorCode errorCode, Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getData() {
        return data;
    }
}