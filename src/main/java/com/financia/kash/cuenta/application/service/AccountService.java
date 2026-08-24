package com.financia.kash.cuenta.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.application.port.input.ManageAccountsUseCase;
import com.financia.kash.cuenta.application.port.output.AccountRepositoryPort;
import com.financia.kash.cuenta.domain.model.Account;
import com.financia.kash.cuenta.domain.model.AccountType;
import com.financia.kash.transaccion.domain.model.TransactionType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountService implements ManageAccountsUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    public Account save(Account account) {
        return accountRepositoryPort.save(account);
    }

    @Override
    public Account findById(UUID id) {
        return accountRepositoryPort.findById(id);
    }

    @Override
    public List<Account> findByUserId(UUID userId) {
        return accountRepositoryPort.findByUserId(userId);
    }

    @Override
    public Account rename(UUID id, String name) {

        Account account = accountRepositoryPort.findById(id);

        account.rename(name);

        return accountRepositoryPort.save(account);
    }

    @Override
    public Account changeType(UUID id, AccountType type) {

        Account account = accountRepositoryPort.findById(id);

        account.changeType(type);

        return accountRepositoryPort.save(account);
    }

    @Override
    public Account applyTransaction(
            UUID id,
            BigDecimal amount,
            TransactionType transactionType
    ) {

        Account account = accountRepositoryPort.findById(id);

        account.applyTransaction(amount, transactionType);

        return accountRepositoryPort.save(account);
    }

    @Override
    public void deactivate(UUID id) {

        Account account = accountRepositoryPort.findById(id);

        account.deactivate();

        accountRepositoryPort.save(account);
    }
}