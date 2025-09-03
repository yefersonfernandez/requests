package com.powerup.security.exceptions;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.UnauthorizedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UnauthorizedExceptionHandler implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.error(new UnauthorizedException(ExceptionMessages.UNAUTHORIZED_ACCESS.getMessage()));
    }
}