package com.powerup.security.config;

import com.powerup.security.exceptions.AccessDeniedExceptionHandler;
import com.powerup.security.exceptions.UnauthorizedExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.powerup.security.constants.SecurityConstants.*;
import static com.powerup.security.constants.SecurityConstants.CLAIM_ROLE;
import static com.powerup.security.constants.SecurityConstants.ROLE_PREFIX;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccessDeniedExceptionHandler accessDeniedHandler;
    private final UnauthorizedExceptionHandler unauthorizedHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, ACTUATOR_HEALTH_URL).permitAll()
                        .pathMatchers(PUBLIC_SWAGGER_PATHS).permitAll()
                        .pathMatchers(HttpMethod.POST, LOAN_CREATION_URL).hasAnyRole(ROLE_CLIENT)
                        .pathMatchers(HttpMethod.GET, LOAN_REVIEW_URL).hasAnyRole(ROLE_ADVISOR)
                        .pathMatchers(HttpMethod.PUT, LOAN_DECISION_URL).hasAnyRole(ROLE_ADVISOR)
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                                .authenticationEntryPoint(unauthorizedHandler)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> Mono.justOrEmpty(jwt.getClaimAsString(CLAIM_ROLE))
                .map(role -> List.<GrantedAuthority>of(new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase())))
                .defaultIfEmpty(Collections.emptyList())
                .map(authorities -> new JwtAuthenticationToken(jwt, authorities));
    }
}
