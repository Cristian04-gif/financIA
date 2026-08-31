package com.financia.kash.cuenta.cuenta.domain.exception;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException(String name) {
        super("La cuenta " + name + " esta inactiva");
    }

}
