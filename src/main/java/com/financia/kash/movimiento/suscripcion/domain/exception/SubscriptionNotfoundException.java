package com.financia.kash.movimiento.suscripcion.domain.exception;

import java.util.UUID;

public class SubscriptionNotfoundException extends RuntimeException {

    public SubscriptionNotfoundException(UUID id) {
        super("La suscripcion con el id '" + id + "' no existe");
    }

}
