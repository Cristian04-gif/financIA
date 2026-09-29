package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.dto.MovementDTO;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface GetMovementUseCase {
    Mono<PaginationResponse<MovementDTO>> getAllMovements(UUID userId, PaginationRequest request);

    Mono<MovementDTO> getMovementById(UUID movementId);
}
