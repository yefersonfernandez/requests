package com.powerup.security.adapter;

import com.powerup.port.token.ISecurityContextPort;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextAdapter implements ISecurityContextPort {
    @Override
    public Mono<String> getAccessToken() {
        return extractJwt().map(Jwt::getTokenValue);
    }

    @Override
    public Mono<String> getUserEmail() {
        return extractJwt().map(Jwt::getSubject);
    }

    private Mono<Jwt> extractJwt() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(JwtAuthenticationToken::getToken);
    }
}
