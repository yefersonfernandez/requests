package com.powerup.api;

import com.powerup.api.dto.request.LoanRequestDto;
import com.powerup.api.mapper.ILoanMapper;
import com.powerup.api.util.ValidatorUtil;
import com.powerup.usecase.loan.LoanUseCase;
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
