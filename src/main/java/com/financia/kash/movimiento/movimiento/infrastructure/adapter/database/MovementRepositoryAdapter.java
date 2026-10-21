package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.meta.application.port.output.MovementSaveForObjective;
import com.financia.kash.movimiento.movimiento.application.port.input.response.MovementDTO;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.domain.exception.MovementNotFoundException;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.mapping.MovementMapper;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.MovementEntityQueryrepository;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.MovementEntityRepository;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepositoryPort, MovementSaveForObjective {

    private final MovementEntityRepository movementEntityRepository;
    private final MovementMapper movementMapper;
    private final MovementEntityQueryrepository movementEntityQueryrepository;

    @Override
    public Mono<PaginationResponse<MovementDTO>> findAllMyMotions(UUID userId, PaginationRequest request) {

        Flux<MovementProject> pageMotion = movementEntityQueryrepository.findAllByUserId(userId, request);

        Mono<Long> count = movementEntityRepository.countByUserId(userId);

        return Mono.zip(pageMotion.map(movementMapper::mapToDomainDTO).collectList(), count).map(tuple -> {
            List<MovementDTO> motions = tuple.getT1();
            long totalElements = tuple.getT2();
            int totalPages = (int) Math.ceil((double) totalElements / request.getPageSize());
            boolean isLast = request.getPageNum() >= Math.max(0, totalPages - 1);

            return new PaginationResponse<>(motions, request.getPageNum(),
                    request.getPageSize(), totalPages, totalElements, isLast);
        });
    }

    @Override
    public Mono<Movement> findById(UUID movementId) {
        return movementEntityRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(movementId))).map(movementMapper::mapToDomain);
    }

    @Override
    public Mono<Movement> save(Movement motion) {
        return Mono.just(movementMapper.mapToEntity(motion)).flatMap(movementEntityRepository::save)
                .map(movementMapper::mapToDomain);
    }

    @Override
    public Mono<Void> delete(UUID movementId) {
        return movementEntityRepository.deleteById(movementId);
    }

    @Override
    public Mono<MovementDTO> findByIdDTO(UUID movementId) {
        return movementEntityRepository.findByIdProject(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(movementId)))
                .map(movementMapper::mapToDomainDTO);
    }

}
