package com.financia.kash.presupuesto.infrastructure.adapter.database.repository;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetCategoryEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BudgetCategoryEntityRepository extends ReactiveCrudRepository<BudgetCategoryEntity, UUID> {
    Flux<BudgetCategoryEntity> findAllByBudgetIdOrderByCategoryIdAsc(UUID budgetId);
    Mono<Void> deleteAllByBudgetId(UUID budgetId);
}

