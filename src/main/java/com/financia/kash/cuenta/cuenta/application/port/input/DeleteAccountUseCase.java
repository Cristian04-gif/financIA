package com.financia.kash.cuenta.cuenta.application.port.input;

import java.util.UUID;

public interface DeleteAccountUseCase {
    void deleteMyAccount(UUID accountId);
}
