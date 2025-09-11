package com.powerup.api.loan;

import com.powerup.api.dto.request.LoanDecisionRequestDto;
import com.powerup.api.dto.request.LoanRequestDto;
import com.powerup.api.mapper.ILoanForReviewMapper;
import com.powerup.api.mapper.ILoanMapper;
import com.powerup.api.util.ValidatorUtil;
import com.powerup.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ILoanForReviewMapper loanForReviewMapper;
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

    public Mono<ServerResponse> getLoansForReview(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        String status = request.queryParam("status").orElse(null);

        Pageable pageable = PageRequest.of(page, size);

        return loanUseCase.getLoansForReviewByStatus(status, page, size)
                .map(loanForReviewMapper::toResponseDto)
                .collectList()
                .map(loansList -> new PageImpl<>(loansList, pageable, loansList.size()))
                .flatMap(pageResult -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pageResult))
                .doOnSubscribe(s -> log.info("Fetching loans for review, status={}, page={}, size={}", status, page, size))
                .doOnError(e -> log.error("Error fetching loans for review", e));
    }

    public Mono<ServerResponse> listenProcessLoanDecision(ServerRequest serverRequest) {
        Long loanId = Long.valueOf(serverRequest.pathVariable("id"));

        return serverRequest.bodyToMono(LoanDecisionRequestDto.class)
                .flatMap(validatorUtil::validate)
                .doOnNext(dto -> log.info("Received loan decision request for loanId={} with decision={}", loanId, dto.decision()))
                .flatMap(dto -> loanUseCase.processLoanDecision(loanId, dto.decision()))
                .doOnSuccess(loan -> log.info("Loan decision processed successfully for loanId={}", loan.getId()))
                .doOnError(error -> log.error("Error while processing loan decision: {}", error.getMessage(), error))
                .map(loanMapper::toLoanResponseDto)
                .flatMap(loanDecisionResult  -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loanDecisionResult));
    }
}
