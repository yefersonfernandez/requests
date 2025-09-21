package com.powerup.consumer;

import com.powerup.consumer.dto.user.response.UserResponseDto;
import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.RemoteServiceException;
import com.powerup.exception.UserNotFoundException;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.token.ISecurityContextPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRestConsumer implements IUserConsumerPort {
    private final WebClient client;
    private final UserConsumerMapper userConsumerMapper;
    private final ISecurityContextPort securityContextPort;

    @CircuitBreaker(name = "getUserByDocument")
    public Mono<UserConsumer> getUserByIdentityDocument(String identityDocument) {
        return securityContextPort.getAccessToken()
                .flatMap(token -> client
                        .get()
                        .uri("/userByIdentityDocument/{identityDocument}", identityDocument)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> handle4xxError(response, identityDocument))
                        .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                        .bodyToMono(UserResponseDto.class)
                        .map(userConsumerMapper::toUserConsumer)
                );
    }

    @CircuitBreaker(name = "getUserByEmail")
    public Mono<UserConsumer> getUserByEmail(String email) {
        return securityContextPort.getAccessToken()
                .flatMap(token -> client
                        .get()
                        .uri("/userByEmail/{email}", email)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> handle4xxError(response, email))
                        .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                        .bodyToMono(UserResponseDto.class)
                        .map(userConsumerMapper::toUserConsumer)
                );
    }

    private Mono<? extends Throwable> handle4xxError(ClientResponse response, String identityDocument) {
        return Mono.error(new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND.format(identityDocument)));
    }

    private Mono<? extends Throwable> handle5xxError(ClientResponse response) {
        return response.createException()
                .flatMap(ex -> Mono.error(new RemoteServiceException(
                        ExceptionMessages.REMOTE_SERVICE_ERROR.getMessage() + ": " + ex.getMessage()
                )));
    }

}
