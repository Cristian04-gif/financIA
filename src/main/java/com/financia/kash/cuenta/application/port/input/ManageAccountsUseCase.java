package com.financia.kash.cuenta.application.port.input;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.domain.model.Account;
import com.financia.kash.cuenta.domain.model.AccountType;
import com.financia.kash.transaccion.domain.model.TransactionType;

public interface ManageAccountsUseCase {

    Account save(Account account);

    Account findById(UUID id);

    List<Account> findByUserId(UUID userId);

    Account rename(UUID id, String name);

    Account changeType(UUID id, AccountType type);

    Account applyTransaction(
            UUID id,
            BigDecimal amount,
            TransactionType transactionType
    );

    void deactivate(UUID id);
}