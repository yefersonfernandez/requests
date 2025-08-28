package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class BusinessException extends RuntimeException {
    private ExceptionStatusCode statusCode;

    public BusinessException(ExceptionStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ExceptionStatusCode getStatusCode() {
        return statusCode;
    }
}
