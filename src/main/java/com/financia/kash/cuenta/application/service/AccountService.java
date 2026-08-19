package com.financia.kash.cuenta.application.service;

import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.application.port.input.ManageAccountsUseCase;
import com.financia.kash.cuenta.application.port.output.AccountRepositoryPort;
import com.financia.kash.cuenta.domain.model.Account;

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
    public void delete(UUID id) {
        accountRepositoryPort.delete(id);
    }
}
