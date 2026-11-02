package com.financia.kash.cuenta.cuenta.application.port.input;

import com.financia.kash.cuenta.cuenta.application.port.input.command.TransferMoneyCommand;

import reactor.core.publisher.Mono;

public interface TransferMoneyUseCase {
    Mono<Void> transfer(TransferMoneyCommand command);
}
