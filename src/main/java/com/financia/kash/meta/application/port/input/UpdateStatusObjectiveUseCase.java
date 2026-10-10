package com.financia.kash.meta.application.port.input;

import com.financia.kash.meta.application.port.input.command.UpdateStatusCommand;

import reactor.core.publisher.Mono;

public interface UpdateStatusObjectiveUseCase {
    Mono<Void> updateStatus(UpdateStatusCommand command);
}
