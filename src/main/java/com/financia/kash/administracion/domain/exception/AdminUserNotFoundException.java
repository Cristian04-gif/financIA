package com.financia.kash.administracion.domain.exception;

import java.util.UUID;

public class AdminUserNotFoundException extends RuntimeException {
    public AdminUserNotFoundException(UUID id) { super("El usuario no existe: " + id); }
}
