package com.powerup.api.exception;

import com.powerup.api.dto.error.CustomError;
import com.powerup.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        CustomError customError;

        if (error instanceof BusinessException businessException) {
            customError = CustomError.builder()
                    .statusCode(businessException.getStatusCode().getStatusCode())
                    .error(businessException.getStatusCode().name())
                    .message(businessException.getMessage())
                    .path(request.path())
                    .timestamp(LocalDateTime.now())
                    .build();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", customError);
            return errorResponse;

        } else if (error instanceof ConstraintViolationException violationException) {
            List<String> errors = toListErrors(violationException.getConstraintViolations());

            customError = CustomError.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.name())
                    .message("Validation failed for one or more fields")
                    .errors(errors)
                    .path(request.path())
                    .timestamp(LocalDateTime.now())
                    .build();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", customError);
            return errorResponse;
        }

        return super.getErrorAttributes(request, options);
    }

    private List<String> toListErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
    }
}