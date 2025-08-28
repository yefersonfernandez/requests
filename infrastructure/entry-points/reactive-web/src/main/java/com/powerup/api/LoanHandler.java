package com.powerup.api;

import com.powerup.api.dto.error.CustomError;
import com.powerup.api.dto.request.LoanRequestDto;
import com.powerup.api.dto.response.LoanResponseDto;
import com.powerup.api.mapper.ILoanMapper;
import com.powerup.api.util.ValidatorUtil;
import com.powerup.usecase.loan.LoanUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanHandler {
    private final LoanUseCase loanUseCase;
    private final ILoanMapper loanMapper;
    private final ValidatorUtil validatorUtil;

    @Operation(
            operationId = "saveLoan",
            summary = "Register a new Loan",
            description = "Registers a new user after validating unique email and salary range",
            requestBody = @RequestBody(
                    content = @Content(schema = @Schema(implementation = LoanRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Loan registered successfully",
                            content = @Content(schema = @Schema(implementation = LoanResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid loan request (e.g., missing or invalid fields)",
                            content = @Content(schema = @Schema(implementation = CustomError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Loan type or user not found",
                            content = @Content(schema = @Schema(implementation = CustomError.class))
                    )
            }
    )
    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanRequestDto.class)
                .flatMap(validatorUtil::validate)
                .map(loanMapper::toModel)
                .doOnNext(loan -> log.debug("Received loan request: {}", loan))
                .flatMap(loanUseCase::saveLoan)
                .doOnSuccess(savedLoan -> log.info("Loan saved successfully with email={}", savedLoan.getEmail()))
                .doOnError(error -> log.error("Error while saving loan: {}", error.getMessage(), error))
                .map(loanMapper::toLoanResponseDto)
                .flatMap(savedLoan -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedLoan));
    }
}
