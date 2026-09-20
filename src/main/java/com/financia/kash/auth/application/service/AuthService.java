package com.financia.kash.auth.application.service;

import java.util.Map;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.input.LoginUserUseCase;
import com.financia.kash.auth.application.port.input.RegisterUserUseCase;
import com.financia.kash.auth.application.port.input.Verify2faUseCase;
import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.application.port.output.UserSaveForAuthPort;
import com.financia.kash.auth.domain.exception.AuthException;
import com.financia.kash.auth.domain.exception.ExistingEmailException;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;
import com.financia.kash.auth.domain.model.TwoFactorAuth;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUserUseCase, RegisterUserUseCase, Verify2faUseCase {

    private final AuthenticationPort authenticationPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserEmailForAuthenticationPort emailForAuthenticationPort;
    private final TwoFactorAuth twoFactorAuth;
    private final ReactiveUserDetailsService userDetailsService;
    private final UserSaveForAuthPort userSaveForAuthPort;

    @Override
    public Mono<AuthResponse> registerUser(Auth auth) {
        return emailForAuthenticationPort.existEmail(auth.getEmail()).flatMap(isRegister -> {
            if (isRegister) {
                return Mono.error(new ExistingEmailException(auth.getEmail()));
            }

            User user = new User(auth.getName(), auth.getLastName(), auth.getEmail(),
                    passwordEncoderPort.ecoderPassword(auth.getPassword()), auth.getRole().toUpperCase());

            return userSaveForAuthPort.save(user).then(authenticationPort.authenticate(
                    auth.getEmail(),
                    auth.getPassword()))
                    .map(AuthResponse::new);

        });
    }

    @Override
    public Mono<Map<String, Object>> loginUser(String email, String password) {
        return emailForAuthenticationPort.findByEmail(email).flatMap(user -> {
            if (user.isEnable2fa()) {
                return authenticationPort
                        .preAuthenticate(email, password)
                        .map(token -> Map.of(
                                "requires2fa", true,
                                "preAuthToken", token));
            }

            return authenticationPort
                    .authenticate(email, password)
                    .map(token -> Map.of(
                            "requires2fa", false,
                            "token", token));

        });
    }

    @Override
    public Mono<AuthResponse> verify2fa(String preToken, String code) {
        return authenticationPort.getUsername(preToken).flatMap(username -> {
            Mono<UserDetails> userDetails = userDetailsService.findByUsername(username);
            Mono<User> userMono = emailForAuthenticationPort.findByEmail(username);

            return Mono.zip(userDetails, userMono).flatMap(tupla -> {
                UserDetails details = tupla.getT1();
                User user = tupla.getT2();
                if (!authenticationPort.validatePreAuthToken(preToken, details)) {
                    return Mono.error(new RuntimeException("Token temporal inválido o expirado"));
                }

                if (!twoFactorAuth.verifyCode(user.getSecret2fa(), code)) {
                    return Mono.error(new AuthException("Código de verificación incorrecto"));
                }

                String finalToken = authenticationPort.generateFinalTokenWithoutPassword(details);
                return Mono.just(new AuthResponse(finalToken));
            });
        });

    }

}
