package com.helpie.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체 에러 코드 관리 Enum
 * 모든 비즈니스 예외의 에러 코드, HTTP 상태, 메시지를 중앙에서 관리
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum ErrorCode {
    
    // === Survey 도메인 에러 ===
    SURVEY_BASIC_INFO_ALREADY_EXISTS(HttpStatus.CONFLICT, "SURVEY_001", "이미 등록된 설문조사 기본정보입니다"),
    SURVEY_BASIC_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_002", "설문조사 기본정보를 찾을 수 없습니다"),
    
    // === 공통 에러 ===
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값 검증에 실패했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류가 발생했습니다");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}