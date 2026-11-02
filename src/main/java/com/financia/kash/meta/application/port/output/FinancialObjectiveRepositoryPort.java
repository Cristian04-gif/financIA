package com.financia.kash.meta.application.port.output;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialObjectiveRepositoryPort {
    Mono<PaginationResponse<FinancialObjectives>> findAllByUserId(UUID userId, PaginationRequest request);

    Flux<FinancialObjectives> findAllByUser(UUID userId, LocalDate from, LocalDate until);

    Mono<FinancialObjectives> findById(UUID id);

    Mono<FinancialObjectives> save(FinancialObjectives financialObjectives);

    Mono<Void> deleteById(UUID id);

    Flux<FinancialObjectives> findExpired(LocalDate date);
}
