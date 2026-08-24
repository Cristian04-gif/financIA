package com.financia.kash.presupuesto.application.port.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;

public interface ManageBudgetsUseCase {

    Budget save(Budget budget);

    Budget findById(UUID id);

    List<Budget> findByUserId(UUID userId);

    Budget updateLimit(
            UUID id,
            BigDecimal limitAmount
    );

    Budget addExpense(
            UUID id,
            BigDecimal amount
    );

    BigDecimal remainingAmount(UUID id);

    boolean isExceeded(UUID id);

    void deactivate(UUID id);
}