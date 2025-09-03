package com.powerup.security.exceptions;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.ForbiddenException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AccessDeniedExceptionHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return Mono.error(new ForbiddenException(ExceptionMessages.FORBIDDEN_OPERATION.getMessage()));
    }
}