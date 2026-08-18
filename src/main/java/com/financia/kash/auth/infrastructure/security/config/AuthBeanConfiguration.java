package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.application.service.AuthService;
import com.financia.kash.auth.application.service.TwoFactorAuthService;
import com.financia.kash.auth.domain.model.TwoFactorAuth;

@Configuration
public class AuthBeanConfiguration {

    @Bean
    public AuthService authService(AuthenticationPort authenticationPort, PasswordEncoderPort passwordEncoderPort,
            UserEmailForAuthenticationPort userEmailForAuthenticationPort,
            ApplicationEventPublisher applicationEventPublisher, TwoFactorAuth twoFactorAuth,
            UserDetailsService userDetailsService) {
        return new AuthService(authenticationPort, passwordEncoderPort, userEmailForAuthenticationPort,
                applicationEventPublisher, twoFactorAuth, userDetailsService);
    }

    @Bean
    public TwoFactorAuthService twoFactorAuthService(UserEmailForAuthenticationPort emailForAuthenticationPort,
            TwoFactorAuth twoFactorAuth, ApplicationEventPublisher applicationEventPublisher) {
        return new TwoFactorAuthService(emailForAuthenticationPort, twoFactorAuth, applicationEventPublisher);
    }
}
