package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface MovementRepositoryPort {
    Mono<PaginationResponse<Motion>> findAllMyMotions(UUID userId, PaginationRequest request);

    Mono<Motion> findById(UUID movementId);

    Mono<Motion> save(Motion motion);

    Mono<Void> delete(UUID movementId);
}
