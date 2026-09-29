package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.domain.exception.MovementNotFoundException;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.movimiento.domain.model.dto.MovementDTO;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.mapping.MovementMapper;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.MovementEntityRepository;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepositoryPort {

    private final MovementEntityRepository movementEntityRepository;
    private final MovementMapper movementMapper;

    @Override
    public Mono<PaginationResponse<MovementDTO>> findAllMyMotions(UUID userId, PaginationRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getPageSize(),
                Sort.by(request.getDirection().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        request.getSortBy()));

        Flux<MovementProject> pageMotion = movementEntityRepository.findAllByUserId(userId, pageRequest);
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
