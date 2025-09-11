package com.powerup.sqs.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.SqsSendMessageException;
import com.powerup.port.sqs.ISqsSenderPort;
import com.powerup.port.sqs.model.LoanDecisionMessage;
import com.powerup.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements ISqsSenderPort {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendMessage(LoanDecisionMessage message) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(message))
                .flatMap(rawMessage -> sendRawMessage(rawMessage, properties.queueUrl(), message))
                .onErrorMap(error -> {
                    log.error("Error sending LoanDecisionMessage. loanId=[{}], clientEmail=[{}], decision=[{}]. Cause: {}",
                            message.getLoanId(),
                            message.getClientEmail(),
                            message.getDecision(),
                            error.getMessage(),
                            error);
                    return new SqsSendMessageException(ExceptionMessages.REMOTE_SERVICE_ERROR.format(error.getMessage()));
                });
    }

    private Mono<Void> sendRawMessage(String rawMessage, String queueUrl, LoanDecisionMessage message) {
        return Mono.fromCallable(() -> buildRequest(rawMessage, queueUrl))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("LoanDecisionMessage sent. loanId=[{}], clientEmail=[{}], decision=[{}]",
                        message.getLoanId(),
                        message.getClientEmail(),
                        message.getDecision())
                ).then();
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}
