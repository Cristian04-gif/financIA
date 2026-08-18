package com.financia.kash.usuario.domain.exception;

import java.util.UUID;

public class User2FAEnabledException extends RuntimeException {

    public User2FAEnabledException(UUID userId) {
        super("El usuario con id '" + userId + "' ya tiene activado la autenticacion 2fa");
    }

}
