package com.financia.kash.cuenta.cuenta.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.Account;

public interface GetAccountUseCase {
    List<Account> getAllMyAccount(UUID userId);

    Account getMyAccountById(UUID accountId);
}
