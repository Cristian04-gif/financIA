package com.financia.kash.meta.infrastructure.database;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.financia.kash.meta.application.port.output.FinancialObjectiveRepositoryPort;
import com.financia.kash.meta.domain.exception.ObjectiveNotFountException;
import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.meta.infrastructure.database.entity.FinancialObjectiveEntity;
import com.financia.kash.meta.infrastructure.database.mapping.FinancialObjectiveMapper;
import com.financia.kash.meta.infrastructure.database.repository.ObjectiveRepository;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ObjectiveRepositoryAdapter implements FinancialObjectiveRepositoryPort {

    private final ObjectiveRepository objectiveRepository;
    private final FinancialObjectiveMapper financialObjectiveMapper;

    @Override
    public Mono<PaginationResponse<FinancialObjectives>> findAllByUserId(UUID userId, PaginationRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getPageSize(),
                Sort.by(request.getDirection().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        request.getSortBy()));

        Flux<FinancialObjectiveEntity> pageObjective = objectiveRepository.findAllByUserId(userId, pageRequest);
        Mono<Long> count = objectiveRepository.countByUserId(userId);

        return Mono.zip(pageObjective.map(financialObjectiveMapper::mapToDomain).collectList(), count).map(tuple -> {
            List<FinancialObjectives> financialObjectives = tuple.getT1();
            long totalElements = tuple.getT2();
            int totalPages = (int) Math.ceil((double) totalElements / request.getPageSize());
            boolean isLast = request.getPageNum() >= Math.max(0, totalPages - 1);

            return new PaginationResponse<>(
                    financialObjectives, request.getPageNum(),
                    request.getPageSize(), totalPages, totalElements, isLast);
        });
    }

    @Override
    public Mono<FinancialObjectives> findById(UUID id) {
        return objectiveRepository.findById(id).switchIfEmpty(Mono.error(new ObjectiveNotFountException(id)))
                .map(financialObjectiveMapper::mapToDomain);
    }

    @Override
    public Mono<FinancialObjectives> save(FinancialObjectives financialObjectives) {
        return Mono.fromCallable(() -> {
            return financialObjectiveMapper.mapToEntity(financialObjectives);
        }).flatMap(objectiveRepository::save).map(financialObjectiveMapper::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return objectiveRepository.deleteById(id);
    }

    @Override
    public Flux<FinancialObjectives> findAllByUser(UUID userId, LocalDate from, LocalDate until) {
        return objectiveRepository.findAllByUserIdAndStartDateBetween(userId, from, until)
                .map(financialObjectiveMapper::mapToDomain);
    }

    @Override
    public Flux<FinancialObjectives> findExpired(LocalDate date) {
        return objectiveRepository.findAllByTargetDate(date).map(financialObjectiveMapper::mapToDomain);
    }
}
