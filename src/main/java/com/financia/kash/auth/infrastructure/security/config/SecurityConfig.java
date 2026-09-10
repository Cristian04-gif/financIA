package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final WebFilter jwtFilter;
        // private final AuthenticationProvider authenticationProvider;
        // private final HandlerExceptionResolver handlerExceptionResolver;

        private final String ENDPOINTS_FREE[] = { "/api/v1/auth/login", "/api/v1/auth/register",
                        "/api/v1/auth/verify-2fa", "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html" };

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(exchange -> exchange.pathMatchers(ENDPOINTS_FREE).permitAll()
                                                .anyExchange().authenticated())
                                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                                .build();
                // .authorizeHttpRequests(auth ->
                // auth.requestMatchers(ENDPOINTS_FREE).permitAll()
                // .anyRequest().authenticated())
                // .sessionManagement(session -> session
                // .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .authenticationProvider(authenticationProvider)
                // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // .exceptionHandling(exception -> exception
                // .authenticationEntryPoint((request, response,
                // authException) -> handlerExceptionResolver
                // .resolveException(request, response,
                // null, authException)))
                // .build();
        }
}
