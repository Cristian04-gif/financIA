package com.financia.kash.meta.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Mono;

public interface RetriveAccountForObjectivesPort {
    Mono<Account> findMyAccountById(UUID id);

    Mono<Account> save(Account account);
}
