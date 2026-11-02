package com.financia.kash.presupuesto.application.port.input;

import java.util.UUID;
import com.financia.kash.presupuesto.application.model.BudgetConsumption;
import reactor.core.publisher.Mono;

public interface GetBudgetConsumptionUseCase {
    Mono<BudgetConsumption> getConsumption(UUID userId, UUID budgetId);
}
