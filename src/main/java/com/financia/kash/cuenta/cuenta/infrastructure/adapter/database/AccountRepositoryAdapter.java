package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.exception.AccountNotFoundException;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.mapping.AccountMapper;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.movimiento.movimiento.application.port.output.AccountForMovementPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRespotoryPort, AccountForMovementPort {

    private final AccountEntityRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<Account> findAllMyAccounts(UUID userId) {
        return accountRepository.findByUserId(userId).stream().map(accountMapper::mapToDomain).toList();
    }

    @Override
    public Account findMyAccountById(UUID accountId) {
        return accountRepository.findById(accountId).map(accountMapper::mapToDomain)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public boolean existsAccount(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = accountMapper.mapToEntity(account);
        AccountEntity save = accountRepository.save(accountEntity);
        return accountMapper.mapToDomain(save);
    }

    @Override
    public void delete(UUID accountId) {
        accountRepository.deleteById(accountId);
    }

}
