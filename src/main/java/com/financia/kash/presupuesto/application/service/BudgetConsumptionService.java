package com.financia.kash.presupuesto.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.financia.kash.presupuesto.application.model.*;
import com.financia.kash.presupuesto.application.port.input.GetBudgetConsumptionUseCase;
import com.financia.kash.presupuesto.application.port.input.GetBudgetUseCase;
import com.financia.kash.presupuesto.application.port.output.BudgetExpensePort;
import com.financia.kash.presupuesto.domain.model.Budget;
import reactor.core.publisher.Mono;

public class BudgetConsumptionService implements GetBudgetConsumptionUseCase {
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private final GetBudgetUseCase budgets;
    private final BudgetExpensePort expenses;

    public BudgetConsumptionService(GetBudgetUseCase budgets, BudgetExpensePort expenses) {
        this.budgets = budgets;
        this.expenses = expenses;
    }

    @Override
    public Mono<BudgetConsumption> getConsumption(UUID userId, UUID budgetId) {
        return budgets.getById(userId, budgetId).flatMap(budget ->
                expenses.findExpensesByCategory(userId, budget.periodStart(), budget.periodEnd())
                        .collectMap(BudgetExpense::categoryId, BudgetExpense::amount)
                        .map(amounts -> calculate(budget, amounts)));
    }

    private BudgetConsumption calculate(Budget budget, Map<UUID, BigDecimal> amounts) {
        BigDecimal spent = amounts.values().stream().reduce(ZERO, BigDecimal::add);
        BigDecimal available = budget.amountLimitTotal().subtract(spent);
        List<BudgetCategoryConsumption> categories = budget.categories().stream().map(category -> {
            BigDecimal categorySpent = amounts.getOrDefault(category.categoryId(), ZERO);
            BigDecimal categoryAvailable = category.amountLimit().subtract(categorySpent);
            return new BudgetCategoryConsumption(category.categoryId(), category.amountLimit(),
                    categorySpent, categoryAvailable, categoryAvailable.negate().max(ZERO));
        }).toList();
        return new BudgetConsumption(budget.id(), budget.amountLimitTotal(), spent,
                available, available.negate().max(ZERO), categories);
    }
}
