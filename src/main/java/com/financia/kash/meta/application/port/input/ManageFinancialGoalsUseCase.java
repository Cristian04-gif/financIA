package com.financia.kash.meta.application.port.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialGoal;

public interface ManageFinancialGoalsUseCase {

    FinancialGoal save(FinancialGoal financialGoal);

    FinancialGoal findById(UUID id);

    List<FinancialGoal> findByUserId(UUID userId);

    FinancialGoal updateTarget(
            UUID id,
            BigDecimal targetAmount,
            LocalDate targetDate
    );

    FinancialGoal addContribution(
            UUID id,
            BigDecimal amount
    );

    BigDecimal remainingAmount(UUID id);

    void cancel(UUID id);
}
