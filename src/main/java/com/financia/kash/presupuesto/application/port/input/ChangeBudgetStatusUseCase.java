package com.financia.kash.presupuesto.application.port.input;

import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;
import reactor.core.publisher.Mono;

public interface ChangeBudgetStatusUseCase {
    Mono<Budget> changeStatus(UUID userId, UUID budgetId, boolean active);
}
