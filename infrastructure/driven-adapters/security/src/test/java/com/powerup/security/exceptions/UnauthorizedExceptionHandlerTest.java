package com.powerup.security.exceptions;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

class UnauthorizedExceptionHandlerTest {

    private UnauthorizedExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UnauthorizedExceptionHandler();
    }

    @Test
    @DisplayName("Should return UnauthorizedException when authentication fails")
    void testCommenceReturnsUnauthorizedException() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        AuthenticationException authEx = mock(AuthenticationException.class);

        StepVerifier.create(handler.commence(exchange, authEx))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UnauthorizedException;
                    assert error.getMessage().equals(ExceptionMessages.UNAUTHORIZED_ACCESS.getMessage());
                })
                .verify();
    }
}
