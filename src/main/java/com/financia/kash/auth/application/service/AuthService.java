package com.financia.kash.auth.application.service;

import org.springframework.context.ApplicationEventPublisher;

import com.financia.kash.auth.application.port.input.LoginUserUseCase;
import com.financia.kash.auth.application.port.input.RegisterUserUseCase;
import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.domain.exception.ExistingEmailException;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;
import com.financia.kash.auth.infrastructure.adapter.event.UserRegistrarionEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthService implements LoginUserUseCase, RegisterUserUseCase {

    private final AuthenticationPort authenticationPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserEmailForAuthenticationPort emailForAuthenticationPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public AuthResponse registerUser(Auth auth) {
        if (emailForAuthenticationPort.existEmail(auth.getEmail())) {
            throw new ExistingEmailException(auth.getEmail());
        }

        Auth authRegister = new Auth(auth.getName(), auth.getLastName(), auth.getEmail(),
                passwordEncoderPort.ecoderPassword(auth.getPassword()), auth.getRole());

        // hacer un evento que guarde al usuario
        eventPublisher.publishEvent(new UserRegistrarionEvent(authRegister));

        String token = authenticationPort.authenticate(auth.getEmail(), auth.getPassword());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse loginUser(String email, String password) {
        emailForAuthenticationPort.findByEmail(email);

        String token = authenticationPort.authenticate(email, password);
        return new AuthResponse(token);
    }

}
