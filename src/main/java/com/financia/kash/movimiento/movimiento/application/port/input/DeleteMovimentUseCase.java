package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface DeleteMovimentUseCase {
    Mono<Void> deleteMovement(UUID movementId);
}
