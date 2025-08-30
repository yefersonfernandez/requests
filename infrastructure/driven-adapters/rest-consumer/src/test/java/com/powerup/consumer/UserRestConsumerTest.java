package com.powerup.consumer;

import com.powerup.port.consumer.model.UserConsumer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

class UserRestConsumerTest {

    private static MockWebServer mockBackEnd;
    private static UserRestConsumer userRestConsumer;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();

        userRestConsumer = new UserRestConsumer(webClient, dto -> UserConsumer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .identityDocument(dto.getIdentityDocument())
                .email(dto.getEmail())
                .baseSalary(dto.getBaseSalary())
                .build());
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

        var result = userRestConsumer.getUserByIdentityDocument("123");

        StepVerifier.create(result)
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
    @DisplayName("Should return empty when user not found")
    void shouldReturnEmptyWhenUserNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value()));

        StepVerifier.create(userRestConsumer.getUserByIdentityDocument("999"))
                .verifyComplete();
    }
}
