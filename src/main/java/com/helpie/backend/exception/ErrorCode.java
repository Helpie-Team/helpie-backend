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

    // == 파일 업로드 도메인 에러 ==
    FAILED_TO_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "FILE_001", "파일 업로드에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_002", "업로드할 파일이 존재하지 않습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "FILE_003", "파일명이 유효하지 않습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_004", "지원하지 않는 파일 형식입니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_005", "업로드 가능한 파일 크기를 초과했습니다."),
    FILE_STREAM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_006", "파일을 읽는 중 오류가 발생했습니다."),

    // == S3/스토리지 에러 ==
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_001", "스토리지 업로드 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_002", "스토리지에서 파일 삭제 중 오류가 발생했습니다."),
    S3_NO_SUCH_BUCKET(HttpStatus.NOT_FOUND, "S3_003", "지정한 버킷을 찾을 수 없습니다."),
    S3_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S3_004", "S3 접근 권한이 없습니다."),
    S3_REGION_MISMATCH(HttpStatus.BAD_REQUEST, "S3_005", "S3 리전 설정이 올바르지 않습니다."),
    S3_NETWORK_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "S3_006", "스토리지 서버와의 통신에 실패했습니다."),

    // === Survey 도메인 에러 ===
    SURVEY_BASIC_INFO_ALREADY_EXISTS(HttpStatus.CONFLICT, "SURVEY_001", "이미 등록된 설문조사 기본정보입니다"),
    SURVEY_BASIC_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_002", "설문조사 기본정보를 찾을 수 없습니다"),
    
    // === ChatRoom 도메인 에러 ===
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_001", "채팅방을 찾을 수 없습니다"),
    CHATROOM_NOT_JOINED(HttpStatus.BAD_REQUEST, "CHATROOM_002", "채팅방에 입장하지 않았습니다"),
    CHATROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHATROOM_003", "채팅방 접근 권한이 없습니다"),

    // == group 도메인 에러==
    NO_GROUP_INFO(HttpStatus.NOT_FOUND, "GROUP_001", "존재하지 않는 소모임입니다"),
     GROUP_ENDED(HttpStatus.NOT_FOUND, "GROUP_001", "이미 완료한 소모임입니다"),

    // === 공통 에러 ===
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값 검증에 실패했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류가 발생했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "접근 권한이 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "데이터베이스 처리 중 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_006", "외부 API 호출 중 오류가 발생했습니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "COMMON_007", "요청 시간이 초과되었습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON_008", "외부 서버로부터 잘못된 응답을 받았습니다.");
    
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