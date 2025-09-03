package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(ExceptionStatusCode.FORBIDDEN, message);
    }
}