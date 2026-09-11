package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface GetMovementUseCase {
    Mono<PaginationResponse<Motion>> getAllMovements(UUID userId, PaginationRequest request);

    Mono<Motion> getMovementById(UUID movementId);
}
