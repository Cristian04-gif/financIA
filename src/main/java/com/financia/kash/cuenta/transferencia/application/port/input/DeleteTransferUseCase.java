package com.financia.kash.cuenta.transferencia.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface DeleteTransferUseCase {
    Mono<Void> deleteTransfer(UUID transferId);
}
