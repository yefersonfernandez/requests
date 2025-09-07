package com.powerup.consumer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RestConsumerConfig.class, RestConsumerConfigTest.WebClientTestConfig.class})
@TestPropertySource(properties = {
        "adapter.restconsumer.url=http://localhost:8080/api",
        "adapter.restconsumer.timeout=5000"
})
class RestConsumerConfigTest {

    @TestConfiguration
    static class WebClientTestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Autowired
    private WebClient webClient;

    @Test
    @DisplayName("Should load context and create WebClient bean")
    void contextLoads() {
        assertThat(webClient).isNotNull();
    }

}
