package com.financia.kash.cuenta.cuenta.application.port.input;

import com.financia.kash.cuenta.cuenta.application.port.input.command.CreateAccountCommand;
import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Mono;

public interface CreateAccountUseCase {
    Mono<Account> createAccount(CreateAccountCommand command);
}
