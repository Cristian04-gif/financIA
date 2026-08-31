package com.financia.kash.cuenta.transferencia.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateTransferUseCase {
    void updateTransfer(UUID transferId, BigDecimal newAmount, String newDescription);
}
