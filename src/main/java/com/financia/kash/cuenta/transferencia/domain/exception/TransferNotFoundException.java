package com.financia.kash.cuenta.transferencia.domain.exception;

import java.util.UUID;

public class TransferNotFoundException extends RuntimeException {

    public TransferNotFoundException(UUID id) {
        super("No se encontro la transferencia con el id: " + id);
    }

}
