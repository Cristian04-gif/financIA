package com.financia.kash.cuenta.cuenta.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.cuenta.cuenta.application.port.input.CreateAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.DeleteAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.GetAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.TransferMoneyUseCase;
import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.domain.model.AccountType;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService
        implements CreateAccountUseCase, DeleteAccountUseCase, GetAccountUseCase, TransferMoneyUseCase {

    private final AccountRespotoryPort accountRespotoryPort;
    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    @Transactional
    public void transfer(UUID idSource, UUID idTarget, BigDecimal amount, String description) {
        Account accountSource = accountRespotoryPort.findMyAccountById(idSource);
        Account accountTarget = accountRespotoryPort.findMyAccountById(idTarget);

        if (accountSource.getId().equals(accountTarget.getId())) {
            throw new IllegalArgumentException("No puedes hacer una trasnferencia entre las mismas cuentas");
        }

        accountSource.validateAccountIsActive();
        accountSource.validateSufficientFunds(amount);

        accountSource.transfer(amount);
        accountTarget.receive(amount);

        Transfer transfer = new Transfer(accountSource.getUserId(), idSource, idTarget, amount, description);
        transferRepositoryPort.save(transfer);

        accountRespotoryPort.save(accountSource);
        accountRespotoryPort.save(accountTarget);

    }

    @Override
    public List<Account> getAllMyAccount(UUID userId) {
        return accountRespotoryPort.findAllMyAccounts(userId);
    }

    @Override
    public Account getMyAccountById(UUID accountId) {
        return accountRespotoryPort.findMyAccountById(accountId);
    }

    @Override
    @Transactional
    public void deleteMyAccount(UUID accountId) {
        accountRespotoryPort.delete(accountId);
    }

    @Override
    public Account createAccount(UUID userId, String name, AccountType type, BigDecimal initialBalance) {
        Account account = new Account(userId, name, type, initialBalance);
        return accountRespotoryPort.save(account);
    }

}
