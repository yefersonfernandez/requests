package com.powerup.security.constants;

public class SecurityConstants {

    private SecurityConstants() {}

    public static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    public static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    public static final String ALGORITHM_RSA = "RSA";

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String CLAIM_ROLE = "role";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_ADVISOR = "ADVISOR";

    public static final String LOAN_CREATION_URL = "/api/v1/loans";
    public static final String LOAN_REVIEW_URL = "/api/v1/loansForReview";
    public static final String LOAN_DECISION_URL = "/api/v1/loans/{id}/decision";
    public static final String[] PUBLIC_SWAGGER_PATHS = {
            "/api/doc/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
    };

}
