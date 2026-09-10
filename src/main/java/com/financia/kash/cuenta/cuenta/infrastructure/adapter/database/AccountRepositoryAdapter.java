package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.exception.AccountNotFoundException;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.mapping.AccountMapper;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.movimiento.movimiento.application.port.output.AccountForMovementPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRespotoryPort, AccountForMovementPort {

    private final AccountEntityRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Flux<Account> findAllMyAccounts(UUID userId) {
        return accountRepository.findByUserId(userId).map(accountMapper::mapToDomain);
    }

    @Override
    public Mono<Account> findMyAccountById(UUID accountId) {
        return accountRepository.findById(accountId).map(accountMapper::mapToDomain)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)));
    }

    @Override
    public Mono<Boolean> existsAccount(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    public Mono<Account> save(Account account) {
        return Mono.just(accountMapper.mapToEntity(account))
                .flatMap(accountEntity -> accountRepository.save(accountEntity)).map(accountMapper::mapToDomain);
    }

    @Override
    public Mono<Void> delete(UUID accountId) {
        return accountRepository.deleteById(accountId);
    }

}
