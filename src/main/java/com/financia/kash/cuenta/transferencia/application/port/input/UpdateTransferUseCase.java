package com.financia.kash.cuenta.transferencia.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface UpdateTransferUseCase {
    Mono<Void> updateTransfer(UUID transferId, BigDecimal newAmount, String newDescription);
}
