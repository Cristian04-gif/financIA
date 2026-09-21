package com.financia.kash.shared.domain.exception;

public class OwnershipException extends RuntimeException {

    public OwnershipException(String email) {
        super("El usuario con correo '" + email + "' no es propietario de este recurso");
    }

}
