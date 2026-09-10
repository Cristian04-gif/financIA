package com.financia.kash.cuenta.cuenta.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface TransferMoneyUseCase {
    Mono<Void> transfer(UUID userId, UUID idSource, UUID idTarget, BigDecimal amount, String description);
}
