package com.financia.kash.meta.application.port.output;

import com.financia.kash.movimiento.movimiento.domain.model.Movement;

import reactor.core.publisher.Mono;

public interface MovementSaveForObjective {
    Mono<Movement> save(Movement movement);
}
