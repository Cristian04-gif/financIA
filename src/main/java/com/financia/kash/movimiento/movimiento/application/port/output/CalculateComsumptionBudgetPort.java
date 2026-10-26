package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.presupuesto.application.model.BudgetConsumption;

import reactor.core.publisher.Mono;

public interface CalculateComsumptionBudgetPort {
    public Mono<BudgetConsumption> calculateConsumption(UUID userId, UUID budgetId);
}
