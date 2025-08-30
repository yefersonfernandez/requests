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

        return switch (error) {
            case BusinessException businessException -> buildErrorResponse(
                    businessException.getStatusCode().getStatusCode(),
                    businessException.getStatusCode().name(),
                    businessException.getMessage(),
                    request.path(),
                    null
            );

            case ConstraintViolationException violationException -> buildErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name(),
                    "Validation failed for one or more fields",
                    request.path(),
                    toListErrors(violationException.getConstraintViolations())
            );

            default -> super.getErrorAttributes(request, options);
        };
    }

    private Map<String, Object> buildErrorResponse(int statusCode, String error, String message, String path, List<String> errors) {
        CustomError customError = CustomError.builder()
                .statusCode(statusCode)
                .error(error)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", customError);
        return errorResponse;
    }

    private List<String> toListErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
    }
}