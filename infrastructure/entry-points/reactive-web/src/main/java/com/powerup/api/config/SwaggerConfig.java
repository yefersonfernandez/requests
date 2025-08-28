package com.powerup.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Request Microservice",
                description = "Handles loan request submission and validation."
        )
)
public class SwaggerConfig { }
