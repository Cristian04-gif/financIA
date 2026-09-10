package com.financia.kash.cuenta.cuenta.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface DeleteAccountUseCase {
    Mono<Void> changeStatusAcount(UUID accountId);

    Mono<Void> deleteMyAccount(UUID accountId);
}
