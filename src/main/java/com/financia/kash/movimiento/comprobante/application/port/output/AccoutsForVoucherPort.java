package com.financia.kash.movimiento.comprobante.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

import reactor.core.publisher.Flux;

public interface AccoutsForVoucherPort {
    Flux<Account> findAllMyAccountsAndType(UUID userId, String typeAccount);

    Flux<Account> findAllMyAccounts(UUID userId);

}
