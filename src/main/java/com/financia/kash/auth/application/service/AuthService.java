package com.financia.kash.auth.application.service;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.input.LoginUserUseCase;
import com.financia.kash.auth.application.port.input.RegisterUserUseCase;
import com.financia.kash.auth.application.port.input.Verify2faUseCase;
import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.domain.exception.AuthException;
import com.financia.kash.auth.domain.exception.ExistingEmailException;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;
import com.financia.kash.auth.domain.model.TwoFactorAuth;
import com.financia.kash.auth.infrastructure.adapter.event.UserRegistrarionEvent;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUserUseCase, RegisterUserUseCase, Verify2faUseCase {

    private final AuthenticationPort authenticationPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserEmailForAuthenticationPort emailForAuthenticationPort;
    private final ApplicationEventPublisher eventPublisher;
    private final TwoFactorAuth twoFactorAuth;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse registerUser(Auth auth) {
        if (emailForAuthenticationPort.existEmail(auth.getEmail())) {
            throw new ExistingEmailException(auth.getEmail());
        }

        Auth authRegister = new Auth(auth.getName(), auth.getLastName(), auth.getEmail(),
                passwordEncoderPort.ecoderPassword(auth.getPassword()), auth.getRole());

        // evento que guarde al usuario
        eventPublisher.publishEvent(new UserRegistrarionEvent(authRegister));

        String token = authenticationPort.authenticate(auth.getEmail(), auth.getPassword());

        return new AuthResponse(token);
    }

    @Override
    public Map<String, Object> loginUser(String email, String password) {
        User user = emailForAuthenticationPort.findByEmail(email);
        if (user.isEnable2fa()) {
            String preAuthToken = authenticationPort.preAuthenticate(email, password);
            return Map.of("requires2fa", true,
                    "preAuthToken", preAuthToken);
        }
        String token = authenticationPort.authenticate(email, password);
        return Map.of("requires2fa", false, "token", token);
    }

    @Override
    public AuthResponse verify2fa(String preToken, String code) {
        String username = authenticationPort.getUsername(preToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!authenticationPort.validatePreAuthToken(preToken, userDetails)) {
            throw new RuntimeException("Token temporal inválido o expirado");
        }

        User user = emailForAuthenticationPort.findByEmail(username);

        if (!twoFactorAuth.verifyCode(user.getSecret2fa(), code)) {
            throw new AuthException("Código de verificación incorrecto");
        }
        String finalToken = authenticationPort.generateFinalTokenWithoutPassword(userDetails);
        return new AuthResponse(finalToken);

    }

}
