package com.financia.kash.movimiento.suscripcion.domain.exception;

public class FrequencyNotFoundException extends RuntimeException {

    public FrequencyNotFoundException() {
        super("La frecuencia de suscripcion escogida no existe");
    }

}
