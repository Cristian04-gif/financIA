package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.application.port.input.response.MovementDTO;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface MovementRepositoryPort {
    Mono<PaginationResponse<MovementDTO>> findAllMyMotions(UUID userId, PaginationRequest request);

    Mono<Movement> findById(UUID movementId);

    Mono<MovementDTO> findByIdDTO(UUID movementId);

    Mono<Movement> save(Movement motion);

    Mono<Void> delete(UUID movementId);
}
