package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.application.port.input.command.Confirm2FACommand;

import reactor.core.publisher.Mono;

public interface Confirm2FARequestUseCase {
    Mono<String> confirm2fa(Confirm2FACommand command);
}
