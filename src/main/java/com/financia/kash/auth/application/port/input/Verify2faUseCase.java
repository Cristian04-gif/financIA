package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.application.port.input.command.Verify2FACommand;
import com.financia.kash.auth.domain.model.AuthResponse;

import reactor.core.publisher.Mono;

public interface Verify2faUseCase {
    Mono<AuthResponse> verify2fa(Verify2FACommand command);
}
