package com.financia.kash.dashboard.user.application.port.output;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;

import reactor.core.publisher.Flux;

public interface RetrieveAllBudgetPort {
    Flux<Budget> findAllUserId(UUID userId, LocalDateTime from, LocalDateTime until);
}
