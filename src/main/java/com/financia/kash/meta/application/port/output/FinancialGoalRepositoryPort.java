package com.financia.kash.meta.application.port.output;

import java.util.List;
import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialGoal;

public interface FinancialGoalRepositoryPort {
    FinancialGoal save(FinancialGoal financialGoal);

    FinancialGoal findById(UUID id);

    List<FinancialGoal> findByUserId(UUID userId);

    void delete(UUID id);
}
