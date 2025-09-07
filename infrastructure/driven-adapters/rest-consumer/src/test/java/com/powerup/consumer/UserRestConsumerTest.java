package com.powerup.consumer;

import com.powerup.consumer.dto.user.response.UserResponseDto;
import com.powerup.exception.RemoteServiceException;
import com.powerup.exception.UserNotFoundException;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.token.ISecurityContextPort;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

class UserRestConsumerTest {

    private static MockWebServer mockBackEnd;
    private static UserRestConsumer userRestConsumer;
    private static ISecurityContextPort securityContextPort;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();

        securityContextPort = Mockito.mock(ISecurityContextPort.class);
        Mockito.when(securityContextPort.getAccessToken())
                .thenReturn(Mono.just("fake-token"));

        UserConsumerMapper mapper = dto -> UserConsumer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .identityDocument(dto.getIdentityDocument())
                .email(dto.getEmail())
                .baseSalary(dto.getBaseSalary())
                .build();

        userRestConsumer = new UserRestConsumer(webClient, mapper, securityContextPort);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Should return UserConsumer when user exists")
    void shouldReturnUserConsumerWhenUserExists() {
        String responseJson = """
            {
              "id": 1,
              "firstName": "John",
              "lastName": "Doe",
              "birthDate": "1990-01-01",
              "address": "123 Main St",
              "phone": "555-1234",
              "identityDocument": "123",
              "email": "john.doe@example.com",
              "baseSalary": 2500.00
            }
            """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseJson));

        StepVerifier.create(userRestConsumer.getUserByIdentityDocument("123"))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                                user.getFirstName().equals("John") &&
                                user.getLastName().equals("Doe") &&
                                user.getBirthDate().equals(LocalDate.of(1990, 1, 1)) &&
                                user.getAddress().equals("123 Main St") &&
                                user.getPhone().equals("555-1234") &&
                                user.getIdentityDocument().equals("123") &&
                                user.getEmail().equals("john.doe@example.com") &&
                                user.getBaseSalary().compareTo(new BigDecimal("2500.00")) == 0
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value()));

        StepVerifier.create(userRestConsumer.getUserByIdentityDocument("999"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw RemoteServiceException on 5xx errors")
    void shouldThrowRemoteServiceExceptionOn5xx() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(userRestConsumer.getUserByIdentityDocument("123"))
                .expectError( RemoteServiceException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return UserConsumer when getUserByEmail finds a user")
    void shouldReturnUserConsumerWhenGetUserByEmailExists() {
        String responseJson = """
        {
          "id": 2,
          "firstName": "Jane",
          "lastName": "Smith",
          "birthDate": "1985-05-15",
          "address": "456 Elm St",
          "phone": "555-5678",
          "identityDocument": "456",
          "email": "jane.smith@example.com",
          "baseSalary": 3000.00
        }
        """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseJson));

        StepVerifier.create(userRestConsumer.getUserByEmail("jane.smith@example.com"))
                .expectNextMatches(user ->
                        user.getId().equals(2L) &&
                                user.getFirstName().equals("Jane") &&
                                user.getLastName().equals("Smith") &&
                                user.getBirthDate().equals(LocalDate.of(1985, 5, 15)) &&
                                user.getAddress().equals("456 Elm St") &&
                                user.getPhone().equals("555-5678") &&
                                user.getIdentityDocument().equals("456") &&
                                user.getEmail().equals("jane.smith@example.com") &&
                                user.getBaseSalary().compareTo(new BigDecimal("3000.00")) == 0
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when email not found")
    void shouldThrowUserNotFoundExceptionWhenEmailNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value()));

        StepVerifier.create(userRestConsumer.getUserByEmail("notfound@example.com"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw RemoteServiceException on getUserByEmail 5xx error")
    void shouldThrowRemoteServiceExceptionOnGetUserByEmail5xx() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(userRestConsumer.getUserByEmail("error@example.com"))
                .expectError(RemoteServiceException.class)
                .verify();
    }
}
