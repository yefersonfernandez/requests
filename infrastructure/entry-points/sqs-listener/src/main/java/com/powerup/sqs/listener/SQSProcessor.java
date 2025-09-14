package com.powerup.sqs.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerup.sqs.listener.dto.LoanDecisionResultDTO;
import com.powerup.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final LoanUseCase loanUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Received SQS message to update loan state: {}", message.body());

        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), LoanDecisionResultDTO.class))
                .flatMap(data -> {
                    log.info("Processing loanId={} with decision={}", data.loanId(), data.decision());
                    return loanUseCase.updateLoanState(data.loanId(), data.decision());
                })
                .doOnError(error -> log.error("Error processing SQS message: {}", message.body(), error))
                .then();
    }
}
