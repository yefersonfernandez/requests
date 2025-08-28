package com.powerup.consumer;

import com.powerup.consumer.dto.user.response.UserResponseDto;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRestConsumer implements IUserConsumerPort {
    private final WebClient client;
    private final UserConsumerMapper userConsumerMapper;

    @CircuitBreaker(name = "getUserByDocument")
    public Mono<UserConsumer> getUserByIdentityDocument(String identityDocument) {
        return client
                .get()
                .uri("/api/v1/users/userByIdentityDocument/{identityDocument}", identityDocument)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .map(userConsumerMapper::toUserConsumer)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty());
    }
}
