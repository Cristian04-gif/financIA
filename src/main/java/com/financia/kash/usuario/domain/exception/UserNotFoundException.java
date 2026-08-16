package com.financia.kash.usuario.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Usuario con id: " + id + " no encontrado");
    }

    public UserNotFoundException(String email) {
        super("Usuario con correo: " + email + " no encontrado");
    }

}
