package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface ChangeStateCommonMovement {
    Mono<Void> deactivateCommonMovement(UUID userid, UUID movementId);

    Mono<Void> activateCommonMovement(UUID userid, UUID movementId);
}
