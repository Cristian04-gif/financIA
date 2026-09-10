package com.financia.kash.usuario.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface ChangePasswordUseCase {
    Mono<Void> changePassword(UUID id, String newPassword);
}
