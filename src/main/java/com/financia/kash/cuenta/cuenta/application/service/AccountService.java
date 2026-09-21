package com.financia.kash.cuenta.cuenta.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.application.port.input.CreateAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.DeleteAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.GetAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.TransferMoneyUseCase;
import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.domain.model.AccountType;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
// import com.financia.kash.shared.application.port.output.UserActiveForAccountPort;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AccountService
        implements CreateAccountUseCase, DeleteAccountUseCase, GetAccountUseCase, TransferMoneyUseCase {

    private final AccountRespotoryPort accountRespotoryPort;
    private final TransferRepositoryPort transferRepositoryPort;
    // private final UserActiveForAccountPort userActiveForAccountPort;

    @Override
    @Transactional
    public Mono<Void> transfer(UUID idSource, UUID idTarget, BigDecimal amount, String description) {

        Mono<Account> accountSource = accountRespotoryPort.findMyAccountById(idSource);
        Mono<Account> accountTarget = accountRespotoryPort.findMyAccountById(idTarget);

        return Mono.zip(accountSource, accountTarget).flatMap(tupla -> {
            Account source = tupla.getT1();
            Account target = tupla.getT2();

            if (source.getId().equals(target.getId())) {
                return Mono.error(
                        new IllegalArgumentException("No puedes hacer una trasnferencia entre las mismas cuentas"));
            }

            source.validateAccountIsActive();
            source.validateSufficientFunds(amount);

            source.transfer(amount);
            target.receive(amount);

            Transfer transfer = new Transfer(source.getUserId(), idSource, idTarget, amount, description);

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
    public Mono<Account> createAccount(UUID userId, String name, AccountType type, BigDecimal initialBalance) {

        Account account = new Account(userId, name, type, initialBalance);
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
