package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;

import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<AuthResponse> registerUser(Auth auth);
}
