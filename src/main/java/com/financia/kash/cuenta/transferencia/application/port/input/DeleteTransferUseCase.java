package com.financia.kash.cuenta.transferencia.application.port.input;

import java.util.UUID;

public interface DeleteTransferUseCase {
    void deleteTransfer(UUID transferId);
}
