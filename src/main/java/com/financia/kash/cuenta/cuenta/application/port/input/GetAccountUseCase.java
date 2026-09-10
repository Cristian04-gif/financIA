package com.financia.kash.cuenta.cuenta.application.port.input;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetAccountUseCase {
    Flux<Account> getAllMyAccount(UUID userId);

    Mono<Account> getMyAccountById(UUID accountId);
}
