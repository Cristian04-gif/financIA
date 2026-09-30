package com.financia.kash.movimiento.suscripcion.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Mono;

public interface FindAccountForSubscription {
    Mono<Account> findMyAccountById(UUID accountId);
}
