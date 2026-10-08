package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.application.port.input.command.AllMovementCommand;
import com.financia.kash.movimiento.movimiento.application.port.input.response.MovementDTO;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface GetMovementUseCase {
    Mono<PaginationResponse<MovementDTO>> getAllMovements(AllMovementCommand command);

    Mono<MovementDTO> getMovementById(UUID movementId);
}
