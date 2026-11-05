package com.financia.kash.movimiento.movimiento.domain.exception;

public class AccountNotBelongUserException extends RuntimeException {

    public AccountNotBelongUserException() {
        super("La cuenta con la que quiere hacer el movimiento no le pertenece");
    }

}
