package com.financia.kash.cuenta.cuenta.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.domain.model.AccountType;

public interface CreateAccountUseCase {
    Account createAccount(UUID userId, String name, AccountType type, BigDecimal initialBalance);
}
