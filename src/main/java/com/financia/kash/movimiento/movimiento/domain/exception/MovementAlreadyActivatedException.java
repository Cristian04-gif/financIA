package com.financia.kash.movimiento.movimiento.domain.exception;

public class MovementAlreadyActivatedException extends RuntimeException {

    public MovementAlreadyActivatedException() {
        super("El movimiento ya se encuentra activado como movimiento comun");
    }

}
