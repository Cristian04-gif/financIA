package com.financia.kash.cuenta.cuenta.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID id) {
        super("No se encontro la cuenta con el id: " + id);
    }

}
