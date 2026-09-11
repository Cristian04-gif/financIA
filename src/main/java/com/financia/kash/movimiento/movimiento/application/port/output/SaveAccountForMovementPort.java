package com.financia.kash.movimiento.movimiento.application.port.output;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Mono;

public interface SaveAccountForMovementPort {
    Mono<Account> save(Account account);
}
