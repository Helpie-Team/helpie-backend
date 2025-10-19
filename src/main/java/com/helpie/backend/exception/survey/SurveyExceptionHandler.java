package com.helpie.backend.exception.survey;

import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 설문조사 도메인 예외 처리 핸들러
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Slf4j
@RestControllerAdvice
public class SurveyExceptionHandler {

    /**
     * 설문조사 기본정보 중복 등록 예외 처리
     */
    @ExceptionHandler(SurveyBasicInfoAlreadyExistsException.class)
    public ResponseEntity<GlobalExceptionHandler.ErrorResponse> handleSurveyBasicInfoAlreadyExists(SurveyBasicInfoAlreadyExistsException e) {
        log.warn("SurveyBasicInfo already exists: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new GlobalExceptionHandler.ErrorResponse(errorCode.getCode(), e.getMessage()));
    }

    /**
     * 설문조사 기본정보 조회 실패 예외 처리
     */
    @ExceptionHandler(SurveyBasicInfoNotFoundException.class)
    public ResponseEntity<GlobalExceptionHandler.ErrorResponse> handleSurveyBasicInfoNotFound(SurveyBasicInfoNotFoundException e) {
        log.warn("SurveyBasicInfo not found: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new GlobalExceptionHandler.ErrorResponse(errorCode.getCode(), e.getMessage()));
    }

}