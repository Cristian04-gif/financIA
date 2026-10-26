package com.financia.kash.dashboard.user.application.port.input;

import com.financia.kash.dashboard.user.application.port.input.command.FinancialEvolutionCommand;
import com.financia.kash.dashboard.user.domain.model.FinancialEvolution;

import reactor.core.publisher.Mono;

public interface FinancialEvolutionUseCase {
    Mono<FinancialEvolution> financialEvolution(FinancialEvolutionCommand command);
}
