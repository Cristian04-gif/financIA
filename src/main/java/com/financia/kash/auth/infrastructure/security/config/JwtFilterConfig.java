package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

import com.financia.kash.auth.application.service.JwtService;
import com.financia.kash.auth.infrastructure.security.filters.JwtFilter;

@Configuration
public class JwtFilterConfig {
    @Bean
    public JwtFilter jwtFilter(
            JwtService jwtService,
            ReactiveUserDetailsService userDetailsService) {

        return new JwtFilter(jwtService, userDetailsService);
    }
}
