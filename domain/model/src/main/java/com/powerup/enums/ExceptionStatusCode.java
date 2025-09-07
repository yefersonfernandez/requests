package com.powerup.enums;

import lombok.Getter;

@Getter
public enum ExceptionStatusCode {

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;

    ExceptionStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}