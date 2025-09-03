package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(ExceptionStatusCode.UNAUTHORIZED, message);
    }
}
