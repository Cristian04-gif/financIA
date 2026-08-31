package com.financia.kash.cuenta.cuenta.application.port.output;

import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

public interface AccountRespotoryPort {
    List<Account> findAllMyAccounts(UUID userId);

    Account findMyAccountById(UUID accountId);

    boolean existsAccount(UUID accountId);

    Account save(Account account);

    void delete(UUID accountId);
}
