package com.financia.kash.movimiento.movimiento.application.port.input;

import com.financia.kash.movimiento.movimiento.application.port.input.command.CreateMovementCommand;
import com.financia.kash.movimiento.movimiento.application.port.input.command.SaveCoucherCommand;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;

import reactor.core.publisher.Mono;

public interface CreateMovimentUseCase {
    Mono<Movement> createMotion(CreateMovementCommand command);

    Mono<Void> saveVoucherFile(SaveCoucherCommand command);
}
