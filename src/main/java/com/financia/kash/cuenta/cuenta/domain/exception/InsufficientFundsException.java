package com.financia.kash.cuenta.cuenta.domain.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String name) {
        super("Su cuenta " + name + " no cuenta con fondos suficientes para hacer esta transferencia");
    }

}
