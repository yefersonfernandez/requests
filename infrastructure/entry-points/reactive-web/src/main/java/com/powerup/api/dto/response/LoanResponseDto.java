package com.powerup.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response DTO for a loan")
public record LoanResponseDto(

        @Schema(description = "Loan amount", example = "15000")
        BigDecimal amount,

        @Schema(description = "Loan term in months", example = "12")
        Integer term,

        @Schema(description = "User's email address", example = "user@example.com")
        String email,

        @Schema(description = "Loan state ID", example = "1")
        Long idLoanState,

        @Schema(description = "Loan type ID", example = "1")
        Long idLoanType

) {}
