package com.financia.kash.auth.domain.exception;

public class InvalidTwoFactorCodeException extends RuntimeException {

    public InvalidTwoFactorCodeException() {
        super("Codigo invalido, Intenta de nuevo");
    }

}
