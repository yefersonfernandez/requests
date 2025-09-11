package com.powerup.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for processing a loan decision")
public record LoanDecisionRequestDto(

        @NotBlank(message = "Decision cannot be blank")
        @Schema(description = "Loan decision, can be 'APPROVED' or 'REJECTED'", example = "APPROVED")
        String decision

) {}