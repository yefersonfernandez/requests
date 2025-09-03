package com.powerup.security.provider;


import com.powerup.security.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtPublicKeyProvider {

    private final Resource publicKeyResource;

    public Mono<RSAPublicKey> loadPublicKey() {
        return Mono.fromCallable(() -> {
            byte[] keyBytes = readKeyBytes(publicKeyResource);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) KeyFactory.getInstance(SecurityConstants.ALGORITHM_RSA).generatePublic(spec);
        });
    }

    private byte[] readKeyBytes(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String keyString = new String(is.readAllBytes())
                    .replace(SecurityConstants.PUBLIC_KEY_HEADER, "")
                    .replace(SecurityConstants.PUBLIC_KEY_FOOTER, "")
                    .replaceAll("\\s", "");
            return Base64.getDecoder().decode(keyString);
        }
    }
}
