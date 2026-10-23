package com.financia.kash.presupuesto.application.port.output;

import java.util.UUID;
import java.time.LocalDateTime;

import com.financia.kash.presupuesto.domain.model.Budget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BudgetRepositoryPort {
    Mono<Budget> save(Budget budget);
    Flux<Budget> findAllByUserId(UUID userId);
    Mono<Budget> findByIdAndUserId(UUID budgetId, UUID userId);
    Mono<Budget> changeStatus(UUID budgetId, UUID userId, boolean active, LocalDateTime updateDate);
}
