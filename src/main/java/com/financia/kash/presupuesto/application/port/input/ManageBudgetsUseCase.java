package com.financia.kash.presupuesto.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;

public interface ManageBudgetsUseCase {
    Budget save(Budget budget);

    Budget findById(UUID id);

    List<Budget> findByUserId(UUID userId);

    void delete(UUID id);
}
