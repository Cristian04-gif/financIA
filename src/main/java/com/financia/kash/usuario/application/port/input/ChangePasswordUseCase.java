package com.financia.kash.usuario.application.port.input;

import com.financia.kash.usuario.application.port.input.command.ChangePasswordCommand;

import reactor.core.publisher.Mono;

public interface ChangePasswordUseCase {
    Mono<Void> changePassword(ChangePasswordCommand command);
}
