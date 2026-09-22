package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.AccountAuthorizationManager;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.TransferAuthorizationManager;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.CategoryAuthorizationManager;
import com.financia.kash.shared.infrastructure.utils.statics.Endpoints;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final WebFilter jwtFilter;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;
        private final CustomAccessDeniedHandler customAccessDeniedHandler;
        private final AccountAuthorizationManager accountAuthorizationManager;
        private final TransferAuthorizationManager transferAuthorizationManager;
        private final CategoryAuthorizationManager categoryAuthorizationManager;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(
                                                exchange -> exchange.pathMatchers(Endpoints.ENDPOINTS_FREE).permitAll()

                                                                // .pathMatchers(Endpoints.ACCESS_USER_ACTIVE)
                                                                // .access(statusAuthorizationManager)

                                                                .pathMatchers(Endpoints.OWNER_ACCESS_ACCOUNT)
                                                                .access(accountAuthorizationManager)

                                                                .pathMatchers(Endpoints.OWNER_ACCESS_TRANSFER)
                                                                .access(transferAuthorizationManager)

                                                                .pathMatchers(Endpoints.OWNER_ACCESS_CATEGORIES)
                                                                .access(categoryAuthorizationManager)

                                                                .anyExchange().authenticated())
                                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(customAccessDeniedHandler))
                                .build();
        }
}
