package com.powerup.security.config;

import com.powerup.security.provider.JwtPublicKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtConfig {

    @Value("${security.jwt.keys.public}")
    private String publicKey;

    @Bean
    public JwtPublicKeyProvider jwtPublicKeyProvider() {
        return new JwtPublicKeyProvider(publicKey);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(JwtPublicKeyProvider jwtPublicKeyProvider) {
        return jwtPublicKeyProvider.loadPublicKey()
                .map(publicKey -> NimbusReactiveJwtDecoder.withPublicKey(publicKey).build())
                .block();
    }
}