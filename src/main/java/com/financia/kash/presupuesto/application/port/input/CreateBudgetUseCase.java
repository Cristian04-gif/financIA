package com.financia.kash.presupuesto.application.port.input;

import java.util.UUID;

import com.financia.kash.presupuesto.application.model.BudgetCommand;
import com.financia.kash.presupuesto.domain.model.Budget;
import reactor.core.publisher.Mono;

public interface CreateBudgetUseCase {
    Mono<Budget> create(UUID userId, BudgetCommand command);
}

