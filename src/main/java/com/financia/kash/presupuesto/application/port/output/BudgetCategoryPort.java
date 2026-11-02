package com.financia.kash.presupuesto.application.port.output;

import java.util.UUID;

import com.financia.kash.presupuesto.application.model.BudgetCategoryInfo;
import reactor.core.publisher.Mono;

public interface BudgetCategoryPort {
    Mono<BudgetCategoryInfo> findById(UUID categoryId);
}
