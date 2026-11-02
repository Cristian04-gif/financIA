package com.financia.kash.meta.domain.exception;

import java.util.UUID;

public class ObjectiveNotFountException extends RuntimeException {

    public ObjectiveNotFountException(UUID id) {
        super("No se encontro la meta financiera con el id: " + id);
    }

}
