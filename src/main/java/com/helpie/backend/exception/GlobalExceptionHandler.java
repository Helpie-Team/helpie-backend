package com.helpie.backend.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 공통 예외 처리를 담당하는 핸들러
 * 도메인별 예외는 각 도메인의 ExceptionHandler에서 처리
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation 실패 예외 처리
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(BindException e) {
        log.warn("Validation failed: {}", e.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), fieldErrors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Unexpected error occurred", e);
        ErrorCode errorCode = e.getErrorCode();
        Map<String, Object> data = e.getData();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), data));
    }

    /**
     * 처리되지 않은 모든 예외에 대한 기본 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * API 에러 응답 구조체
     */
    public static class ErrorResponse {
        private final String code;
        private final String message;
        private final LocalDateTime timestamp;
        private final Map<String, String> fieldErrors;
        private final Object data;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.fieldErrors = null;
            this.data = null;
        }

        public ErrorResponse(String code, String message, Map<String, String> fieldErrors) {
            this.code = code;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.fieldErrors = fieldErrors;
            this.data = null;
        }

        public ErrorResponse(String code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.fieldErrors = null;
            this.data = data;
        }


        public String getCode() { return code; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, String> getFieldErrors() { return fieldErrors; }
        public Object getData() { return data; }
    }
}