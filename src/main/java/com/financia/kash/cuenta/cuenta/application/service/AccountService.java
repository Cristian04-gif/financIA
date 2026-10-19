package com.financia.kash.cuenta.cuenta.application.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.cuenta.cuenta.application.port.input.CreateAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.DeleteAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.GetAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.TransferMoneyUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.command.CreateAccountCommand;
import com.financia.kash.cuenta.cuenta.application.port.input.command.TransferMoneyCommand;
import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.shared.domain.exception.RedisException;
import com.financia.kash.shared.infrastructure.utils.statics.CacheKeys;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AccountService
        implements CreateAccountUseCase, DeleteAccountUseCase, GetAccountUseCase, TransferMoneyUseCase {

    private final AccountRespotoryPort accountRespotoryPort;
    private final TransferRepositoryPort transferRepositoryPort;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Mono<Void> transfer(TransferMoneyCommand command) {

        Mono<Account> accountSource = accountRespotoryPort.findMyAccountById(command.idSource());
        Mono<Account> accountTarget = accountRespotoryPort.findMyAccountById(command.idTarget());

        return Mono.zip(accountSource, accountTarget).flatMap(tupla -> {
            Account source = tupla.getT1();
            Account target = tupla.getT2();

            if (source.getId().equals(target.getId())) {
                return Mono.error(
                        new IllegalArgumentException("No puedes hacer una trasnferencia entre las mismas cuentas"));
            }

            source.validateAccountIsActive();
            source.validateSufficientFunds(command.amount());

            source.transfer(command.amount());
            target.receive(command.amount());

            Transfer transfer = new Transfer(source.getUserId(), command.idSource(), command.idTarget(),
                    command.amount(), command.description());

            return transferRepositoryPort.save(transfer).then(accountRespotoryPort.save(source))
                    .then(accountRespotoryPort.save(target)).then(deleteAllCache(transfer.getUserId()));
        }).then();

    }

    @Override
    public Flux<Account> getAllMyAccount(UUID userId) {
        String cacheKey = CacheKeys.allUserAccounts(userId);
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(json -> {
                    try {
                        List<Account> accounts = objectMapper.readValue(json, new TypeReference<List<Account>>() {
                        });

                        return Flux.fromIterable(accounts);
                    } catch (JsonProcessingException e) {
                        return Flux.error(new RedisException(e));
                    }
                })
                .switchIfEmpty(accountRespotoryPort.findAllMyAccounts(userId)
                        .collectList()
                        .flatMapMany(accounts -> {
                            try {
                                String json = objectMapper.writeValueAsString(accounts);
                                return redisTemplate.opsForValue()
                                        .set(cacheKey, json, Duration.ofMinutes(15))
                                        .thenMany(Flux.fromIterable(accounts));
                            } catch (JsonProcessingException e) {
                                return Flux.fromIterable(accounts);
                            }
                        }));
    }

    @Override
    public Mono<Account> getMyAccountById(UUID accountId) {
        String cacheKey = CacheKeys.account(accountId);
        return redisTemplate.opsForValue().get(cacheKey).map(json -> {
            try {
                return objectMapper.readValue(json, Account.class);
            } catch (JsonProcessingException e) {
                throw new RedisException(e);
            }
        }).switchIfEmpty(accountRespotoryPort.findMyAccountById(accountId).flatMap(account -> {
            try {
                String json = objectMapper.writeValueAsString(account);
                return redisTemplate.opsForValue()
                        .set(cacheKey, json, Duration.ofMinutes(15))
                        .thenReturn(account);
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        }));
    }

    @Override
    @Transactional
    public Mono<Void> deleteMyAccount(UUID accountId) {

        return accountRespotoryPort.findMyAccountById(accountId)
                .flatMap(account -> accountRespotoryPort.delete(accountId));
    }

    @Override
    public Mono<Account> createAccount(CreateAccountCommand command) {
        Account account = new Account(command.userId(), command.name(), command.type(), command.initialBalance());

        return accountRespotoryPort.save(account).flatMap(accountSaved -> {

            Mono<Long> borrarTodo = deleteAllCache(accountSaved.getUserId());

            return Mono.when(borrarTodo).thenReturn(accountSaved);
        });
    }

    @Override
    public Mono<Void> changeStatusAcount(UUID accountId) {
        return getMyAccountById(accountId).flatMap(account -> {
            account.changeStatus();
            return accountRespotoryPort.save(account).flatMap(accountSaved -> {
                Mono<Boolean> borrarIndividual = deleteInidividual(accountId);
                Mono<Long> borrarTodo = deleteAllCache(accountSaved.getUserId());
                return Mono.when(borrarIndividual, borrarTodo).thenReturn(accountSaved);
            }).then();
        });

    }

    // delete cache
    private Mono<Long> deleteAllCache(UUID userId) {
        Flux<String> llaves = redisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(CacheKeys.allUserAccounts(userId))
                        .build());
        return redisTemplate.delete(llaves);
    }

    private Mono<Boolean> deleteInidividual(UUID accountId) {
        String cacheIndividual = CacheKeys.account(accountId);
        return redisTemplate.opsForValue().delete(cacheIndividual);

    }
}
