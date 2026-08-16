package com.financia.kash.usuario.domain.exception;

public class UserRoleNotfoundException extends RuntimeException {

    public UserRoleNotfoundException(String role) {
        super("El rol " + role + " no existe");
    }

}
