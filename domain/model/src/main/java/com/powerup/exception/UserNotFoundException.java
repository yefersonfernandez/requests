package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message);
    }
}
