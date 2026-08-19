package com.financia.kash.presupuesto.application.service;

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
    public void delete(UUID id) {
        budgetRepositoryPort.delete(id);
    }
}
