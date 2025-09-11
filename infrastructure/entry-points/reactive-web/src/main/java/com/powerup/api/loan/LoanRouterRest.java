package com.powerup.api.loan;

import com.powerup.api.config.LoanPath;
import com.powerup.api.openapi.LoanOpenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;


@Configuration
@RequiredArgsConstructor
public class LoanRouterRest {

    private final LoanPath loanPath;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanHandler handler) {
        return route()
                .POST(loanPath.getLoans(), handler::listenSaveLoan, LoanOpenApi::saveLoan)
                .GET(loanPath.getLoansForReview(), handler::getLoansForReview, LoanOpenApi::getLoansForReview)
                .PUT(loanPath.getLoanDecision(), handler::listenProcessLoanDecision ,LoanOpenApi::processLoanDecision)
                .build();
    }
}
