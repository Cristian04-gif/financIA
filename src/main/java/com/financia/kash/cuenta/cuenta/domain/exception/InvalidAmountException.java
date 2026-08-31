package com.financia.kash.cuenta.cuenta.domain.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(BigDecimal amount) {
        super("Transferencia envalida! No puede hacer una transferencia de S/" + amount);
    }

}
