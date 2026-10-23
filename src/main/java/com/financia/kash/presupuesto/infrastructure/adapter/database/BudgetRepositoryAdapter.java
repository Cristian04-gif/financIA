package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.domain.exception.BudgetNotFoundException;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import com.financia.kash.presupuesto.infrastructure.adapter.database.mapping.BudgetPersistenceMapper;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetCategoryEntityRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetEntityRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BudgetRepositoryAdapter implements BudgetRepositoryPort {
    private final BudgetEntityRepository budgets;
    private final BudgetCategoryEntityRepository categories;
    private final BudgetPersistenceMapper mapper;
    private final TransactionalOperator budgetTransactionalOperator;
    private final TransactionalOperator budgetReadTransactionalOperator;

    public BudgetRepositoryAdapter(BudgetEntityRepository budgets, BudgetCategoryEntityRepository categories,
            BudgetPersistenceMapper mapper,
            @Qualifier("budgetTransactionalOperator") TransactionalOperator writeOperator,
            @Qualifier("budgetReadTransactionalOperator") TransactionalOperator readOperator) {
        this.budgets = budgets;
        this.categories = categories;
        this.mapper = mapper;
        this.budgetTransactionalOperator = writeOperator;
        this.budgetReadTransactionalOperator = readOperator;
    }

    @Override
    public Mono<Budget> save(Budget budget) {
        return Mono.defer(() -> {
            Mono<BudgetEntity> header;
            if (budget.id() == null) {
                header = budgets.save(mapper.toEntity(budget));
            } else {
                header = budgets.findOwnedForUpdate(budget.id(), budget.userId())
                        .switchIfEmpty(Mono.error(new BudgetNotFoundException(budget.id())))
                        .flatMap(existing -> {
                            BudgetEntity updated = mapper.toEntity(budget);
                            updated.setCreationDate(existing.getCreationDate());
                            updated.setActive(existing.isActive());
                            return budgets.save(updated);
                        });
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
        return budgets.findAllByUserIdOrderByCreationDateDescIdAsc(userId).concatMap(this::hydrate)
                .as(budgetReadTransactionalOperator::transactional);
    }

    @Override
    public Mono<Budget> findByIdAndUserId(UUID budgetId, UUID userId) {
        return budgets.findByIdAndUserId(budgetId, userId).flatMap(this::hydrate)
                .as(budgetReadTransactionalOperator::transactional);
    }

    @Override
    public Mono<Budget> changeStatus(UUID budgetId, UUID userId, boolean active, LocalDateTime updateDate) {
        return budgets.findOwnedForUpdate(budgetId, userId).flatMap(existing -> {
            existing.setActive(active);
            existing.setUpdateDate(mapper.databaseTimestamp(updateDate));
            return budgets.save(existing).flatMap(this::hydrate);
        }).as(budgetTransactionalOperator::transactional);
    }

    private Mono<Budget> hydrate(BudgetEntity entity) {
        return categories.findAllByBudgetIdOrderByCategoryIdAsc(entity.getId())
                .map(mapper::toDomain).collectList().map(allocations -> mapper.toDomain(entity, allocations));
    }
}
