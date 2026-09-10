package com.financia.kash.movimiento.movimiento.domain.exception;

public class InvalidMovementTypeException extends RuntimeException {

    public InvalidMovementTypeException(String typeMovement) {
        super("Tipo de movimiento invalido: " + typeMovement);
    }

}
