package com.financia.kash.cuenta.transferencia.application.port.input;

import com.financia.kash.cuenta.transferencia.application.port.input.command.UpdateTransferCommand;

import reactor.core.publisher.Mono;

public interface UpdateTransferUseCase {
    Mono<Void> updateTransfer(UpdateTransferCommand command);
}
