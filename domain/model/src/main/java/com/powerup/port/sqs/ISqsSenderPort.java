package com.powerup.port.sqs;

import com.powerup.port.sqs.model.CapacityValidationMessage;
import com.powerup.port.sqs.model.LoanDecisionMessage;
import reactor.core.publisher.Mono;

public interface ISqsSenderPort {
    Mono<Void> sendMessage(LoanDecisionMessage message);
    Mono<Void> sendCapacityValidationMessage(CapacityValidationMessage message);
}
