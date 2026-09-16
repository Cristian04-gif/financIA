package com.financia.kash.shared.infrastructure.utils.statics;

public class Endpoints {
    public final static String ENDPOINTS_FREE[] = { "/api/v1/auth/login", "/api/v1/auth/register",
            "/api/v1/auth/verify-2fa", "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html" };

    public final static String OWNER_ACCESS_ACCOUNT[] = {
            "/api/v1/accounts/my-accounts/{id}"
    };

    public final static String OWNER_ACCESS_TRANSFER[] = {
            "/api/v1/transfers/{id}"
    };
}
