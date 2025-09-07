package com.powerup.port.consumer;

import com.powerup.port.consumer.model.UserConsumer;
import reactor.core.publisher.Mono;

public interface IUserConsumerPort {
    Mono<UserConsumer> getUserByIdentityDocument(String document );
    Mono<UserConsumer> getUserByEmail(String email);
}
