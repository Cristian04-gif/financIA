package com.financia.kash.cuenta.cuenta.application.service;

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

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AccountService
        implements CreateAccountUseCase, DeleteAccountUseCase, GetAccountUseCase, TransferMoneyUseCase {

    private final AccountRespotoryPort accountRespotoryPort;
    private final TransferRepositoryPort transferRepositoryPort;

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
                    .then(accountRespotoryPort.save(target));
        }).then();

    }

    @Override
    public Flux<Account> getAllMyAccount(UUID userId) {
        return accountRespotoryPort.findAllMyAccounts(userId);
    }

    @Override
    public Mono<Account> getMyAccountById(UUID accountId) {

        return accountRespotoryPort.findMyAccountById(accountId);

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
        return accountRespotoryPort.save(account);

    }

    @Override
    public Mono<Void> changeStatusAcount(UUID accountId) {

        return getMyAccountById(accountId).flatMap(account -> {
            account.changeStatus();
            return accountRespotoryPort.save(account).then();
        });

    }

}
