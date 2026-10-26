package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.dashboard.user.domain.model.ExpensesCategory.CategoriesExcenses;
import com.financia.kash.dashboard.user.domain.model.MovementsMonth;
import com.financia.kash.meta.application.port.output.MovementSaveForObjective;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.application.port.response.MovementDTO;
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

    @Override
    public Flux<Movement> findAllByCategory(UUID userId, UUID categoryId, LocalDateTime from, LocalDateTime until) {
        return movementEntityRepository
                .findAllByUserIdAndCategoryIdAndCreationDateBetween(userId, categoryId, from, until)
                .map(movementMapper::mapToDomain);
    }

    @Override
    public Mono<PaginationResponse<MovementDTO>> findAllByCategory(UUID userId, UUID categoryId,
            PaginationRequest request) {
        Flux<MovementProject> pageMotion = movementEntityQueryrepository.findAllByUserIdAndCategoryId(userId,
                categoryId,
                request);
        Mono<Long> countMono = movementEntityRepository.countByUserIdAndCategoryId(userId, categoryId);
        return Mono.zip(pageMotion.map(movementMapper::mapToDomainDTO).collectList(), countMono).map(tuple -> {
            List<MovementDTO> motions = tuple.getT1();
            long totalElements = tuple.getT2();
            int totalPages = (int) Math.ceil((double) totalElements / request.getPageSize());
            boolean isLast = request.getPageNum() >= Math.max(0, totalPages - 1);

            return new PaginationResponse<>(motions, request.getPageNum(),
                    request.getPageSize(), totalPages, totalElements, isLast);
        });
    }

    @Override
    public Flux<MovementsMonth> findPeriod(UUID userId, Integer limit) {
        return movementEntityQueryrepository.findByPeriod(userId, limit);
    }

    @Override
    public Flux<CategoriesExcenses> findExpensesByCategory(UUID userId, LocalDate from, LocalDate until) {
        return movementEntityQueryrepository.findExpensesByCategory(userId, from, until);
    }

}
