package com.powerup.api;

import com.powerup.api.config.LoanPath;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final LoanPath loanPath;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/loans", produces = {MediaType.APPLICATION_JSON_VALUE,}, method = RequestMethod.POST, beanClass = LoanHandler.class, beanMethod = "listenSaveLoan"),
    })
    public RouterFunction<ServerResponse> routerFunction(LoanHandler handler) {
        return route(POST(loanPath.getLoans()), handler::listenSaveLoan);
    }
}
