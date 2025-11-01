package com.helpie.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체 에러 코드 관리 Enum
 * 모든 비즈니스 예외의 에러 코드, HTTP 상태, 메시지를 중앙에서 관리
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Getter
public enum ErrorCode {

    // === Token 에러 ===
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "TOKEN_001","토큰이 존재하지 않습니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "TOKEN_002","유효하지 않은 토큰 입니다."),
    TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "TOKEN_003","토큰이 만료 되었습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_004","찾을 수 없는 refresh token입니다."),

    // == 유저 도메인 에러 ==
    ALREADY_EXIST_MEMBER(HttpStatus.CONFLICT, "USER_001","이미 존재하는 사용자입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 사용중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_003", "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_004", "비밀번호가 일치하지 않습니다"),

    // == 일반 로그인 도메인 에러 ==
    USER_LOCKED(HttpStatus.CONFLICT, "BASIC_LOGIN_001", "잠긴 계정입니다."),

    // == Email 인증 에러 ===
    EMAIL_AUTH_INVALID(HttpStatus.UNAUTHORIZED, "EMAIL_001", "유효하지 않은 이메일 인증 요청입니다."),

    // === Social Login 에러 ===
    NOT_MATCH_SOCIAL_MEMBER(HttpStatus.UNAUTHORIZED, "SOCIAL_001", ""),
    NOT_MATCH_OAUTH_CODE(HttpStatus.UNAUTHORIZED, "SOCIAL_002", "인증 code가 존재하지 않습니다."),
    NOT_ALLOW_OAUTH_REDIRECT_URI(HttpStatus.BAD_REQUEST, "SOCIAL_003","승인되지 않은 redirectURI입니다."),
    
    // === Survey 도메인 에러 ===
    SURVEY_BASIC_INFO_ALREADY_EXISTS(HttpStatus.CONFLICT, "SURVEY_001", "이미 등록된 설문조사 기본정보입니다"),
    SURVEY_BASIC_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_002", "설문조사 기본정보를 찾을 수 없습니다"),
    
    // === ChatRoom 도메인 에러 ===
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_001", "채팅방을 찾을 수 없습니다"),
    CHATROOM_NOT_JOINED(HttpStatus.BAD_REQUEST, "CHATROOM_002", "채팅방에 입장하지 않았습니다"),
    CHATROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHATROOM_003", "채팅방 접근 권한이 없습니다"),
    
    // === 공통 에러 ===
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값 검증에 실패했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류가 발생했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "접근 권한이 없습니다.");
    
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