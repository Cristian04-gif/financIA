package com.financia.kash.dashboard.user.application.port.output;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialObjectives;

import reactor.core.publisher.Flux;

public interface RetrieveAllGoalsPort {
    Flux<FinancialObjectives> retrieveAllGoals(UUID userId, LocalDate from, LocalDate until);
}
