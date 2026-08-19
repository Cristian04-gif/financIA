package com.financia.kash.cuenta.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.domain.model.Account;

public interface ManageAccountsUseCase {
    Account save(Account account);

    Account findById(UUID id);

    List<Account> findByUserId(UUID userId);

    void delete(UUID id);
}
