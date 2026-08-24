package com.financia.kash.meta.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public FinancialGoal updateTarget(
            UUID id,
            BigDecimal targetAmount,
            LocalDate targetDate
    ) {

        FinancialGoal goal = financialGoalRepositoryPort.findById(id);

        goal.updateTarget(targetAmount, targetDate);

        return financialGoalRepositoryPort.save(goal);
    }

    @Override
    public FinancialGoal addContribution(
            UUID id,
            BigDecimal amount
    ) {

        FinancialGoal goal = financialGoalRepositoryPort.findById(id);

        goal.addContribution(amount);

        return financialGoalRepositoryPort.save(goal);
    }

    @Override
    public BigDecimal remainingAmount(UUID id) {

        FinancialGoal goal = financialGoalRepositoryPort.findById(id);

        return goal.remainingAmount();
    }

    @Override
    public void cancel(UUID id) {

        FinancialGoal goal = financialGoalRepositoryPort.findById(id);

        goal.cancel();

        financialGoalRepositoryPort.save(goal);
    }
}
