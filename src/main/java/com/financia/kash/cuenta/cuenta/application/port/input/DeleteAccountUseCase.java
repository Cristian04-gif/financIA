package com.financia.kash.cuenta.cuenta.application.port.input;

import java.util.UUID;

public interface DeleteAccountUseCase {
    void changeStatusAcount(UUID accountId);

    void deleteMyAccount(UUID accountId);
}
