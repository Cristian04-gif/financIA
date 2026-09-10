package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

public interface AccountForMovementPort {
    Account findMyAccountById(UUID accountId);
}
