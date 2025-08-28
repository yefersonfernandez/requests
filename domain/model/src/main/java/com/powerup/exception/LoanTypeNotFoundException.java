package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class LoanTypeNotFoundException extends BusinessException {
    public LoanTypeNotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message);
    }
}
