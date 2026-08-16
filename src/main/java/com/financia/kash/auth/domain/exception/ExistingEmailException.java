package com.financia.kash.auth.domain.exception;

public class ExistingEmailException extends RuntimeException {

    public ExistingEmailException(String email) {
        super("El correo " + email + " ya existe");
    }

}
