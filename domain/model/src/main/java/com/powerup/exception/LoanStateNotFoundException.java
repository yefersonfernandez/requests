package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class LoanStateNotFoundException extends BusinessException {
    public LoanStateNotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message);
    }
}
