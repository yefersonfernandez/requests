package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class LoanNotFoundException extends BusinessException {
    public LoanNotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message);
    }
}
