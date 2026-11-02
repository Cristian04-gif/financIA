package com.financia.kash.meta.application.port.input;

import com.financia.kash.meta.application.port.input.command.CreateObjectiveCommand;
import com.financia.kash.meta.domain.model.FinancialObjectives;

import reactor.core.publisher.Mono;

public interface CreateObjectiveUseCase {
    Mono<FinancialObjectives> createObjective(CreateObjectiveCommand command);
}
