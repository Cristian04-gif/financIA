package com.financia.kash.movimiento.movimiento.application.port.input;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.application.port.input.command.AllMovementCommand;
import com.financia.kash.movimiento.movimiento.application.port.response.MovementDTO;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetMovementUseCase {
        Mono<PaginationResponse<MovementDTO>> getAllMovements(AllMovementCommand command);

        Mono<MovementDTO> getMovementById(UUID movementId);

        Flux<Movement> retrieveAllTransactionByCategory(UUID userId, UUID categoryId, LocalDateTime from,
                        LocalDateTime until);

        Mono<PaginationResponse<MovementDTO>> retrieveAllTransactionByCategory(UUID userId, UUID categoryId,
                        PaginationRequest request);
}
