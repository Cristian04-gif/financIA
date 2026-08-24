package com.financia.kash.presupuesto.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.financia.kash.presupuesto.application.port.input.ManageBudgetsUseCase;
import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.domain.model.Budget;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetService implements ManageBudgetsUseCase {

    private final BudgetRepositoryPort budgetRepositoryPort;

    @Override
    public Budget save(Budget budget) {
        return budgetRepositoryPort.save(budget);
    }

    @Override
    public Budget findById(UUID id) {
        return budgetRepositoryPort.findById(id);
    }

    @Override
    public List<Budget> findByUserId(UUID userId) {
        return budgetRepositoryPort.findByUserId(userId);
    }

    @Override
    public Budget updateLimit(
            UUID id,
            BigDecimal limitAmount
    ) {

        Budget budget = budgetRepositoryPort.findById(id);

        budget.updateLimit(limitAmount);

        return budgetRepositoryPort.save(budget);
    }

    @Override
    public Budget addExpense(
            UUID id,
            BigDecimal amount
    ) {

        Budget budget = budgetRepositoryPort.findById(id);

        budget.addExpense(amount);

        return budgetRepositoryPort.save(budget);
    }

    @Override
    public BigDecimal remainingAmount(UUID id) {

        Budget budget = budgetRepositoryPort.findById(id);

        return budget.remainingAmount();
    }

    @Override
    public boolean isExceeded(UUID id) {

        Budget budget = budgetRepositoryPort.findById(id);

        return budget.isExceeded();
    }

    @Override
    public void deactivate(UUID id) {

        Budget budget = budgetRepositoryPort.findById(id);

        budget.deactivate();

        budgetRepositoryPort.save(budget);
    }
}