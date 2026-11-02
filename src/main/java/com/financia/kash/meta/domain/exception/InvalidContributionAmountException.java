package com.financia.kash.meta.domain.exception;

public class InvalidContributionAmountException extends RuntimeException {

    public InvalidContributionAmountException() {
        super("El monto tiene que ser positivo para poder ser aportado");
    }

}
