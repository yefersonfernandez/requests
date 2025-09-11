package com.powerup.exception;


import com.powerup.enums.ExceptionStatusCode;

public class SqsSendMessageException extends BusinessException {
    public SqsSendMessageException(String message) {
        super(ExceptionStatusCode.INTERNAL_SERVER_ERROR, message);
    }
}