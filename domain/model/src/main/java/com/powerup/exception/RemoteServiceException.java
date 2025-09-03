package com.powerup.exception;

import com.powerup.enums.ExceptionStatusCode;

public class RemoteServiceException extends BusinessException {
    public RemoteServiceException(String message) {
        super(ExceptionStatusCode.INTERNAL_SERVER_ERROR, message);
    }
}
