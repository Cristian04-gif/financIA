package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface UserForMovementPort {
    Mono<Boolean> isUserActive(UUID userId);
}
