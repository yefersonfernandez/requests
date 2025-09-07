package com.powerup.security.exceptions;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

class AccessDeniedExceptionHandlerTest {

    private AccessDeniedExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AccessDeniedExceptionHandler();
    }

    @Test
    @DisplayName("Should return ForbiddenException when access is denied")
    void testHandleReturnsForbiddenException() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        AccessDeniedException accessDenied = new AccessDeniedException("Denied");

        StepVerifier.create(handler.handle(exchange, accessDenied))
                .expectErrorSatisfies(error -> {
                    assert error instanceof ForbiddenException;
                    assert error.getMessage().equals(ExceptionMessages.FORBIDDEN_OPERATION.getMessage());
                })
                .verify();
    }
}
