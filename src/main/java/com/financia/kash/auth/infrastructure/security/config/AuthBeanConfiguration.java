package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.application.service.AuthService;

@Configuration
public class AuthBeanConfiguration {

    @Bean
    public AuthService authService(AuthenticationPort authenticationPort, PasswordEncoderPort passwordEncoderPort,
            UserEmailForAuthenticationPort emailForAuthenticationPort, ApplicationEventPublisher eventPublisher) {
        return new AuthService(authenticationPort, passwordEncoderPort, emailForAuthenticationPort, eventPublisher);
    }
}
