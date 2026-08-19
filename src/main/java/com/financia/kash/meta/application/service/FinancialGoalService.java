package com.financia.kash.meta.application.service;

import java.util.List;
import java.util.UUID;

import com.financia.kash.meta.application.port.input.ManageFinancialGoalsUseCase;
import com.financia.kash.meta.application.port.output.FinancialGoalRepositoryPort;
import com.financia.kash.meta.domain.model.FinancialGoal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinancialGoalService implements ManageFinancialGoalsUseCase {

    private final FinancialGoalRepositoryPort financialGoalRepositoryPort;

    @Override
    public FinancialGoal save(FinancialGoal financialGoal) {
        return financialGoalRepositoryPort.save(financialGoal);
    }

    @Override
    public FinancialGoal findById(UUID id) {
        return financialGoalRepositoryPort.findById(id);
    }

    @Override
    public List<FinancialGoal> findByUserId(UUID userId) {
        return financialGoalRepositoryPort.findByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        financialGoalRepositoryPort.delete(id);
    }
}
