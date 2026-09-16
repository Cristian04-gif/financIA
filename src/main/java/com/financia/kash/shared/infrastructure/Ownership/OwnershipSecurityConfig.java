package com.financia.kash.shared.infrastructure.Ownership;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.financia.kash.shared.infrastructure.utils.statics.Endpoints;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OwnershipSecurityConfig {

    private final OwnershipAuthorizationManager authorizationManager;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth.pathMatchers(Endpoints.ENDPOINTS_FREE).permitAll()

                        .pathMatchers(Endpoints.OWNER_ACCESS_ACCOUNT)
                        .access(authorizationManager.forResource(ResourceType.ACCOUNT))

                        .pathMatchers(Endpoints.OWNER_ACCESS_TRANSFER)
                        .access(authorizationManager.forResource(ResourceType.TRANSFER))
                        .anyExchange().authenticated())
                .build();
    }
}
