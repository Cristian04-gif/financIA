package com.financia.kash.movimiento.movimiento.domain.exception;

import java.util.UUID;

public class MovementNotFoundException extends RuntimeException {

    public MovementNotFoundException(UUID id) {
        super("El movimiento con id '" + id + "' no se encontro");
    }

}
