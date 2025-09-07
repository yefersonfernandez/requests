package com.powerup.security.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConstantsTest {

    @Test
    @DisplayName("Should have correct RSA constants")
    void testRsaConstants() {
        assertThat(SecurityConstants.PUBLIC_KEY_HEADER).isEqualTo("-----BEGIN PUBLIC KEY-----");
        assertThat(SecurityConstants.PUBLIC_KEY_FOOTER).isEqualTo("-----END PUBLIC KEY-----");
        assertThat(SecurityConstants.ALGORITHM_RSA).isEqualTo("RSA");
    }

    @Test
    @DisplayName("Should have correct role-related constants")
    void testRoleConstants() {
        assertThat(SecurityConstants.ROLE_PREFIX).isEqualTo("ROLE_");
        assertThat(SecurityConstants.CLAIM_ROLE).isEqualTo("role");
        assertThat(SecurityConstants.ROLE_CLIENT).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("Should have correct loan URL")
    void testLoanCreationUrl() {
        assertThat(SecurityConstants.LOAN_CREATION_URL).isEqualTo("/api/v1/loans");
    }

    @Test
    @DisplayName("Should contain expected public swagger paths")
    void testSwaggerPaths() {
        assertThat(SecurityConstants.PUBLIC_SWAGGER_PATHS)
                .containsExactly(
                        "/api/doc/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**"
                );
    }

    @Test
    @DisplayName("Constructor should be private")
    void testPrivateConstructor() throws Exception {
        var constructor = SecurityConstants.class.getDeclaredConstructor();
        assertThat(constructor.canAccess(null)).isFalse();
    }
}
