package com.powerup.api.openapi;

import com.powerup.api.dto.error.CustomError;
import com.powerup.api.dto.request.LoanDecisionRequestDto;
import com.powerup.api.dto.request.LoanRequestDto;
import com.powerup.api.dto.response.LoanForReviewResponseDto;
import com.powerup.api.dto.response.LoanResponseDto;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springdoc.core.fn.builders.securityrequirement.Builder.securityRequirementBuilder;

@UtilityClass
public class LoanOpenApi {

    private final String TAG = "Loan";

    private final String SUCCESS_CODE = String.valueOf(HttpStatus.OK.value());
    private final String BAD_REQUEST_CODE = String.valueOf(HttpStatus.BAD_REQUEST.value());
    private final String NOT_FOUND_CODE = String.valueOf(HttpStatus.NOT_FOUND.value());

    private final String BAD_REQUEST = HttpStatus.BAD_REQUEST.getReasonPhrase();
    private final String NOT_FOUND = HttpStatus.NOT_FOUND.getReasonPhrase();

    private final String PARAM_STATUS = "status";
    private final String PARAM_PAGE = "page";
    private final String PARAM_SIZE = "size";

    private final String DEFAULT_PAGE_DESC = "Page number for pagination, default 0";
    private final String DEFAULT_SIZE_DESC = "Page size for pagination, default 10";
    private final String STATUS_DESC = "Filter loans by status (e.g., APPROVED, PENDING_REVIEW, MANUAL_REVIEW, REJECTED)";


    public Builder saveLoan(Builder builder) {
        return builder
                .operationId("saveLoan")
                .description("Registers a new loan")
                .tag(TAG)
                .security(securityRequirementBuilder().name("bearerAuth"))
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoanRequestDto.class))))
                .response(responseBuilder().responseCode(SUCCESS_CODE).description("Loan registered successfully")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoanResponseDto.class))))
                .response(responseBuilder().responseCode(BAD_REQUEST_CODE).description(BAD_REQUEST)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))))
                .response(responseBuilder().responseCode(NOT_FOUND_CODE).description(NOT_FOUND)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))));
    }

    public Builder getLoansForReview(Builder builder) {
        return builder
                .operationId("getLoansForReview")
                .description("Retrieves a list of loans filtered by status for review")
                .tag(TAG)
                .security(securityRequirementBuilder().name("bearerAuth"))
                .parameter(parameterBuilder()
                        .name(PARAM_STATUS)
                        .description(STATUS_DESC)
                        .required(false))
                .parameter(parameterBuilder()
                        .name(PARAM_PAGE)
                        .description(DEFAULT_PAGE_DESC)
                        .required(false))
                .parameter(parameterBuilder()
                        .name(PARAM_SIZE)
                        .description(DEFAULT_SIZE_DESC)
                        .required(false))
                .response(responseBuilder().responseCode(SUCCESS_CODE).description("Loans retrieved successfully")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoanForReviewResponseDto.class))))
                .response(responseBuilder().responseCode(BAD_REQUEST_CODE).description(BAD_REQUEST)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))))
                .response(responseBuilder().responseCode(NOT_FOUND_CODE).description(NOT_FOUND)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))));
    }

    public Builder processLoanDecision(Builder builder) {
        return builder
                .operationId("processLoanDecision")
                .description("Allows an advisor to approve or reject a loan. The applicant receives an email notification with the final decision.")
                .tag(TAG)
                .security(securityRequirementBuilder().name("bearerAuth"))
                .parameter(parameterBuilder()
                        .name("id")
                        .in(ParameterIn.PATH)
                        .description("ID of the loan to update")
                        .required(true))
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoanDecisionRequestDto.class))))
                .response(responseBuilder().responseCode(SUCCESS_CODE).description("Loan decision processed successfully")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoanResponseDto.class))))
                .response(responseBuilder().responseCode(BAD_REQUEST_CODE).description(BAD_REQUEST)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))))
                .response(responseBuilder().responseCode(NOT_FOUND_CODE).description(NOT_FOUND)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(CustomError.class))));
    }

}