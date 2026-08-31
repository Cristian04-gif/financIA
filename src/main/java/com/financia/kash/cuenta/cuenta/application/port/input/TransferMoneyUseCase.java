package com.financia.kash.cuenta.cuenta.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferMoneyUseCase {
    void transfer(UUID idSource, UUID idTarget, BigDecimal amount);
}
