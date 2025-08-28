package com.powerup.api.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for creating a loan")
public record LoanRequestDto(

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        @Schema(description = "Loan amount requested", example = "15000")
        BigDecimal amount,

        @NotNull(message = "Term cannot be null")
        @Positive(message = "Term must be positive")
        @Schema(description = "Loan term in months", example = "12")
        Integer term,

        @NotBlank(message = "Identity document cannot be blank")
        @Schema(description = "User's identity document", example = "1234567890")
        String identityDocument,

        @NotNull(message = "Loan type cannot be null")
        @Schema(description = "Type of loan requested", example = "1")
        Long idLoanType

) {}
