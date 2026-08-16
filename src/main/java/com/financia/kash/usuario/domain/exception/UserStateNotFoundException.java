package com.financia.kash.usuario.domain.exception;

public class UserStateNotFoundException extends RuntimeException {

    public UserStateNotFoundException(String status) {
        super("El estado " + status + " no existe");
    }

}
