package com.powerup.security.provider;

import com.powerup.security.constants.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import static org.mockito.Mockito.when;

class JwtPublicKeyProviderTest {

    @Mock
    private Resource publicKeyResource;

    private JwtPublicKeyProvider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new JwtPublicKeyProvider(publicKeyResource);
    }

    @Test
    @DisplayName("Should load public key successfully from resource")
    void testLoadPublicKeySuccess() throws Exception {
        String base64Key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK5yYl3Qp6qVQ2BdzEoX7ztVqjEz9MQJxydpDVEiCyXqRGRqC/3h/7KpZIXxzUjRfE/dA4Rrfn7KN9JXqOlti+MCAwEAAQ==";
        String pem = SecurityConstants.PUBLIC_KEY_HEADER + "\n" + base64Key + "\n" + SecurityConstants.PUBLIC_KEY_FOOTER;

        InputStream is = new ByteArrayInputStream(pem.getBytes());
        when(publicKeyResource.getInputStream()).thenReturn(is);

        StepVerifier.create(provider.loadPublicKey())
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should emit error when resource cannot be read")
    void testLoadPublicKeyFailure() throws Exception {
        when(publicKeyResource.getInputStream()).thenThrow(new RuntimeException("File not found"));

        StepVerifier.create(provider.loadPublicKey())
                .expectError(RuntimeException.class)
                .verify();
    }
}
