package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepositoryPort {

    @Override
    public Mono<PaginationResponse<Motion>> findAllMyMotions(UUID userId, PaginationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllMyMotions'");
    }

    @Override
    public Mono<Motion> findById(UUID movementId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Mono<Motion> save(Motion motion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Mono<Void> delete(UUID movementId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
