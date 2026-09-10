package com.financia.kash.movimiento.movimiento.domain.exception;

import java.util.UUID;

public class MovementAlreadyDeactivatedException extends RuntimeException {

    public MovementAlreadyDeactivatedException(UUID movementId) {
        super("El movimiento con id: '" + movementId + "' ya se encuentra desactivado como movimiento comun");
    }

}
