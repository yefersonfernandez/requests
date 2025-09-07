package com.powerup.security.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;

class SecurityContextAdapterTest {

    private SecurityContextAdapter securityContextAdapter;
    private Jwt jwt;
    private JwtAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        securityContextAdapter = new SecurityContextAdapter();

        jwt = new Jwt(
                "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJyb2xlIjoi",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user@gmail.com", "role", "CLIENT")
        );

        authentication = new JwtAuthenticationToken(jwt);
    }

    @Test
    @DisplayName("Should return access token when security context has JWT")
    void testGetAccessTokenWithJwt() {
        Mono<String> result = securityContextAdapter.getAccessToken()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl(authentication))));

        StepVerifier.create(result)
                .expectNext("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJyb2xlIjoi")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return user email when security context has JWT")
    void testGetUserEmailWithJwt() {
        Mono<String> result = securityContextAdapter.getUserEmail()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl(authentication))));

        StepVerifier.create(result)
                .expectNext("user@gmail.com")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty Mono when no security context is present")
    void testGetAccessTokenWithoutJwt() {
        Mono<String> result = securityContextAdapter.getAccessToken();

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty Mono when no JWT authentication is present")
    void testGetUserEmailWithoutJwt() {
        Mono<String> result = securityContextAdapter.getUserEmail();

        StepVerifier.create(result)
                .verifyComplete();
    }
}
