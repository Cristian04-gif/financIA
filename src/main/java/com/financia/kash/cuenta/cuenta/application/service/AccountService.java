package com.financia.kash.cuenta.cuenta.application.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.financia.kash.notificacion.application.service.NotificationService;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
import com.financia.kash.shared.domain.utils.statics.NotificationMessage;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService
                implements CreateAccountUseCase, DeleteAccountUseCase, GetAccountUseCase, TransferMoneyUseCase {

        private final AccountRespotoryPort accountRespotoryPort;
        private final TransferRepositoryPort transferRepositoryPort;
        private final CacheService<Account> cacheService;
        private final NotificationService notificationService;

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
                                                new IllegalArgumentException(
                                                                "No puedes hacer una trasnferencia entre las mismas cuentas"));
                        }

                        source.validateAccountIsActive();
                        source.validateSufficientFunds(command.amount());

                        source.transfer(command.amount());
                        target.receive(command.amount());

                        Transfer transfer = new Transfer(source.getUserId(), command.idSource(), command.idTarget(),
                                        command.amount(), command.description());

                        return transferRepositoryPort.save(transfer)
                                        .flatMap(trans -> accountRespotoryPort.save(source)
                                                        .then(accountRespotoryPort.save(target).thenReturn(trans)));

                }).doOnSuccess(trans -> {
                        CacheKeys.clearAllCacheTransfers(
                                        cacheService.redisTemplate(),
                                        trans.getUserId(),
                                        trans.getSourceAccount(),
                                        trans.getDestinationAccount(),
                                        trans.getId());
                        System.out.println("se borro cache");

                        Map<String, Object> metadata = Map.of(
                                        "transferId", trans.getId(),
                                        "sourceAccount", trans.getSourceAccount(),
                                        "destinationAccount", trans.getDestinationAccount(),
                                        "amount", trans.getAmount());

                        notificationService.sendToUser(command.username(),
                                        NotificationMessage.sendTransferNotification(metadata));
                        System.out.println("se envio notificacion");
                }).then();

        }

        @Override
        public Flux<Account> getAllMyAccount(UUID userId) {
                String cacheKey = CacheKeys.allUserAccounts(userId);
                return cacheService.getCacheList(cacheKey, Account.class)
                                .switchIfEmpty(accountRespotoryPort.findAllMyAccounts(userId)
                                                .collectList()
                                                .flatMapMany(accounts -> cacheService.saveListCache(cacheKey,
                                                                accounts)));
        }

        @Override
        public Mono<Account> getMyAccountById(UUID accountId) {
                String cacheKey = CacheKeys.account(accountId);
                return cacheService.retrieveMonoCache(cacheKey, Account.class)
                                .switchIfEmpty(accountRespotoryPort.findMyAccountById(accountId)
                                                .flatMap(account -> cacheService.saveMonoCache(cacheKey, account)));
        }

        @Override
        @Transactional
        public Mono<Void> deleteMyAccount(UUID accountId) {
                return accountRespotoryPort.findMyAccountById(accountId)
                                .flatMap(account -> accountRespotoryPort.delete(accountId)
                                                .then(CacheKeys.clearAllCacheAccounts(cacheService.redisTemplate(),
                                                                account.getUserId(), accountId)))
                                .then();
        }

        @Override
        public Mono<Account> createAccount(CreateAccountCommand command) {
                Account account = new Account(command.userId(), command.name(), command.type(),
                                command.initialBalance());

                return accountRespotoryPort.save(account)
                                .flatMap(accountSaved -> CacheKeys.clearAllCacheAccounts(cacheService.redisTemplate(),
                                                accountSaved.getUserId(), accountSaved.getId())
                                                .thenReturn(accountSaved));
        }

        @Override
        public Mono<Void> changeStatusAcount(UUID accountId) {
                return getMyAccountById(accountId).flatMap(account -> {
                        account.changeStatus();
                        return accountRespotoryPort.save(account).flatMap(accountSaved -> CacheKeys
                                        .clearAllCacheAccounts(cacheService.redisTemplate(), accountSaved.getUserId(),
                                                        accountId))
                                        .then();
                });

        }

}
