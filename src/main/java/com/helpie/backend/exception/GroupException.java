package com.helpie.backend.exception;

public class GroupException extends BusinessException {

    public GroupException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GroupException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
