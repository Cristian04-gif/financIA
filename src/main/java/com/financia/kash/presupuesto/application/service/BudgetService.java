package com.financia.kash.presupuesto.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.dashboard.user.application.port.output.RetrieveAllBudgetPort;
import com.financia.kash.presupuesto.application.model.BudgetCommand;
import com.financia.kash.presupuesto.application.port.input.ChangeBudgetStatusUseCase;
import com.financia.kash.presupuesto.application.port.input.CreateBudgetUseCase;
import com.financia.kash.presupuesto.application.port.input.GetBudgetUseCase;
import com.financia.kash.presupuesto.application.port.input.UpdateBudgetUseCase;
import com.financia.kash.presupuesto.application.port.output.BudgetCategoryPort;
import com.financia.kash.presupuesto.application.port.output.BudgetOwnerPort;
import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.domain.exception.BudgetAccessDeniedException;
import com.financia.kash.presupuesto.domain.exception.BudgetNotFoundException;
import com.financia.kash.presupuesto.domain.exception.BudgetValidationException;
import com.financia.kash.presupuesto.domain.model.Budget;

import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BudgetService implements CreateBudgetUseCase, GetBudgetUseCase,
        UpdateBudgetUseCase, ChangeBudgetStatusUseCase, RetrieveAllBudgetPort {

    private final BudgetRepositoryPort repository;
    private final BudgetCategoryPort categoryPort;
    private final BudgetOwnerPort ownerPort;
    private final Clock clock;
    private final CacheService<Budget> cacheService;

    public BudgetService(BudgetRepositoryPort repository, BudgetCategoryPort categoryPort,
            BudgetOwnerPort ownerPort, Clock clock) {
        this(repository, categoryPort, ownerPort, clock, null);
    }

    public BudgetService(BudgetRepositoryPort repository, BudgetCategoryPort categoryPort,
            BudgetOwnerPort ownerPort, Clock clock, CacheService<Budget> cacheService) {
        this.repository = repository;
        this.categoryPort = categoryPort;
        this.ownerPort = ownerPort;
        this.clock = clock;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<Budget> create(UUID userId, BudgetCommand command) {
        return requireActiveOwner(userId).then(Mono.defer(() -> {
            Budget budget = Budget.create(userId, command.name(), command.periodStart(),
                    command.periodEnd(), command.amountLimitTotal(), command.categories(),
                    LocalDateTime.now(clock));
            return validateCategories(budget).then(Mono.defer(() -> repository.save(budget)))
                    .flatMap(saved -> {
                        if (cacheService == null) {
                            return Mono.just(saved);
                        }
                        return CacheKeys.clearAllCacheBudgets(cacheService.redisTemplate(), userId, saved.id())
                                .thenReturn(saved);
                    });
        }));
    }

    @Override
    public Flux<Budget> getAll(UUID userId) {
        if (cacheService == null) {
            return requireActiveOwner(userId).thenMany(Flux.defer(() -> repository.findAllByUserId(userId)));
        }
        String cacheKey = CacheKeys.allUserBudgets(userId);
        return requireActiveOwner(userId).thenMany(
                cacheService.getCacheList(cacheKey, Budget.class)
                        .switchIfEmpty(repository.findAllByUserId(userId).collectList()
                                .flatMapMany(budgets -> cacheService.saveListCache(cacheKey, budgets))));
    }

    @Override
    public Mono<Budget> getById(UUID userId, UUID budgetId) {
        if (cacheService == null) {
            return requireActiveOwner(userId).then(Mono.defer(() -> repository.findByIdAndUserId(budgetId, userId)))
                    .switchIfEmpty(Mono.error(new BudgetNotFoundException(budgetId)));
        }
        String cacheKey = CacheKeys.budget(budgetId);
        return requireActiveOwner(userId).then(
                cacheService.retrieveMonoCache(cacheKey, Budget.class)
                        .switchIfEmpty(repository.findByIdAndUserId(budgetId, userId)
                                .flatMap(budget -> cacheService.saveMonoCache(cacheKey, budget)))
                        .switchIfEmpty(Mono.error(new BudgetNotFoundException(budgetId))));
    }

    @Override
    public Mono<Budget> update(UUID userId, UUID budgetId, BudgetCommand command) {
        return getById(userId, budgetId).flatMap(original -> {
            Budget updated = original.update(command.name(), command.periodStart(), command.periodEnd(),
                    command.amountLimitTotal(), command.categories(), LocalDateTime.now(clock));
            return validateCategories(updated).then(Mono.defer(() -> repository.save(updated)))
                    .flatMap(saved -> {
                        if (cacheService == null) {
                            return Mono.just(saved);
                        }
                        return CacheKeys.clearAllCacheBudgets(cacheService.redisTemplate(), userId, budgetId)
                                .thenReturn(saved);
                    });
        });
    }

    @Override
    public Mono<Budget> changeStatus(UUID userId, UUID budgetId, boolean active) {
        return requireActiveOwner(userId)
                .then(Mono.defer(() -> repository.changeStatus(budgetId, userId, active, LocalDateTime.now(clock))))
                .switchIfEmpty(Mono.error(new BudgetNotFoundException(budgetId)))
                .flatMap(saved -> {
                    if (cacheService == null) {
                        return Mono.just(saved);
                    }
                    return CacheKeys.clearAllCacheBudgets(cacheService.redisTemplate(), userId, budgetId)
                            .thenReturn(saved);
                });
    }

    private Mono<Void> requireActiveOwner(UUID userId) {
        if (userId == null) {
            return Mono.error(new BudgetAccessDeniedException());
        }
        return Mono.defer(() -> ownerPort.isActive(userId)).filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new BudgetAccessDeniedException())).then();
    }

    private Mono<Void> validateCategories(Budget budget) {
        return Flux.fromIterable(budget.categories())
                .concatMap(allocation -> categoryPort.findById(allocation.categoryId())
                        .switchIfEmpty(Mono.error(new BudgetValidationException("La categoria no existe")))
                        .flatMap(category -> {
                            if (!category.active() || !category.expense()
                                    || (category.userId() != null && !category.userId().equals(budget.userId()))) {
                                return Mono.error(new BudgetValidationException(
                                        "La categoria debe estar activa, ser de gastos y pertenecer al usuario o ser global"));
                            }
                            return Mono.just(category);
                        }))
                .then();
    }

    @Override
    public Flux<Budget> findAllUserId(UUID userId, LocalDateTime from, LocalDateTime until) {
        return requireActiveOwner(userId).thenMany(Flux.defer(() -> repository.findAllByUserId(userId, from, until)));
    }

    @Override
    public Mono<Void> updateExpiringToday() {
        return repository.findAllBudgetExpiringToday(LocalDate.now()).flatMap(budget -> {
            budget.changeStatus(false, LocalDateTime.now(clock));
            return repository.save(budget);
        }).then();
    }

}
