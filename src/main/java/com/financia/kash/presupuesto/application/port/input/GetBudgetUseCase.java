package com.financia.kash.presupuesto.application.port.input;

import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetBudgetUseCase {
    Flux<Budget> getAll(UUID userId);
    Mono<Budget> getById(UUID userId, UUID budgetId);
}
