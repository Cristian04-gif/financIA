package com.financia.kash.meta.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialGoal;

public interface ManageFinancialGoalsUseCase {
    FinancialGoal save(FinancialGoal financialGoal);

    FinancialGoal findById(UUID id);

    List<FinancialGoal> findByUserId(UUID userId);

    void delete(UUID id);
}
