package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

public interface MovementRepositoryPort {
    PaginationResponse<Motion> findAllMyMotions(UUID userId, PaginationRequest request);

    Motion findById(UUID movementId);

    Motion save(Motion motion);

    void delete(UUID movementId);
}
