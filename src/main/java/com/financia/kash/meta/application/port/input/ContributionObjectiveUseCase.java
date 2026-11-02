package com.financia.kash.meta.application.port.input;

import com.financia.kash.meta.application.port.input.command.ContributionObjectiveCommand;

import reactor.core.publisher.Mono;

public interface ContributionObjectiveUseCase {
    Mono<Void> contributionObjective(ContributionObjectiveCommand command);
}
