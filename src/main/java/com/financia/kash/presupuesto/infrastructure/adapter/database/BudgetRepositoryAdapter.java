package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.domain.exception.BudgetNotFoundException;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import com.financia.kash.presupuesto.infrastructure.adapter.database.mapping.BudgetPersistenceMapper;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetCategoryEntityRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetEntityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepositoryPort {
    private final BudgetEntityRepository budgets;
    private final BudgetCategoryEntityRepository categories;
    private final BudgetPersistenceMapper mapper;
    private final TransactionalOperator budgetTransactionalOperator;

    @Override
    public Mono<Budget> save(Budget budget) {
        return Mono.defer(() -> {
            Mono<BudgetEntity> header;
            if (budget.id() == null) {
                header = budgets.save(mapper.toEntity(budget));
            } else {
                header = budgets.findOwnedForUpdate(budget.id(), budget.userId())
                        .switchIfEmpty(Mono.error(new BudgetNotFoundException(budget.id())))
                        .flatMap(existing -> budgets.save(mapper.toEntity(budget)));
            }
            return header.flatMap(saved -> categories.deleteAllByBudgetId(saved.getId())
                    .thenMany(Flux.fromIterable(budget.categories()).concatMap(category ->
                            categories.save(mapper.toEntity(category, saved.getId()))))
                    .map(mapper::toDomain).collectList()
                    .map(allocations -> mapper.toDomain(saved, allocations)));
        }).as(budgetTransactionalOperator::transactional);
    }

    @Override
    public Flux<Budget> findAllByUserId(UUID userId) {
        return budgets.findAllByUserIdOrderByCreationDateDescIdAsc(userId).concatMap(this::hydrate);
    }

    @Override
    public Mono<Budget> findByIdAndUserId(UUID budgetId, UUID userId) {
        return budgets.findByIdAndUserId(budgetId, userId).flatMap(this::hydrate);
    }

    @Override
    public Mono<Budget> changeStatus(UUID budgetId, UUID userId, boolean active, LocalDateTime updateDate) {
        return budgets.findOwnedForUpdate(budgetId, userId).flatMap(existing -> {
            existing.setActive(active);
            existing.setUpdateDate(updateDate);
            return budgets.save(existing).flatMap(this::hydrate);
        }).as(budgetTransactionalOperator::transactional);
    }

    private Mono<Budget> hydrate(BudgetEntity entity) {
        return categories.findAllByBudgetIdOrderByCategoryIdAsc(entity.getId())
                .map(mapper::toDomain).collectList().map(allocations -> mapper.toDomain(entity, allocations));
    }
}

