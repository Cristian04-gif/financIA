package com.financia.kash.movimiento.movimiento.application.port.output;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.application.port.input.response.MovementDTO;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementRepositoryPort {
    Mono<PaginationResponse<MovementDTO>> findAllMyMotions(UUID userId, PaginationRequest request);

    Flux<Movement> findAll(UUID userId, LocalDateTime from, LocalDateTime until);

    Flux<Movement> findAllByCategory(UUID userId, UUID categoryId, LocalDateTime from, LocalDateTime until);

    Mono<PaginationResponse<MovementDTO>> findAllByCategory(UUID userId, UUID categoryId, PaginationRequest request);

    Mono<Movement> findById(UUID movementId);

    Mono<MovementDTO> findByIdDTO(UUID movementId);

    Mono<Movement> save(Movement motion);

    Mono<Void> delete(UUID movementId);
}
