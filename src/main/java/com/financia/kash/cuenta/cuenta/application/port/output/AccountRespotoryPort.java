package com.financia.kash.cuenta.cuenta.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRespotoryPort {
    Flux<Account> findAllMyAccounts(UUID userId);

    Mono<Account> findMyAccountById(UUID accountId);

    Mono<Boolean> existsAccount(UUID accountId);

    Mono<Account> save(Account account);

    Mono<Void> delete(UUID accountId);
}
