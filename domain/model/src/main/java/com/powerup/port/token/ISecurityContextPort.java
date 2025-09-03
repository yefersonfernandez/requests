package com.powerup.port.token;

import reactor.core.publisher.Mono;

public interface ISecurityContextPort {
    Mono<String> getAccessToken();
    Mono<String> getUserEmail();
}
