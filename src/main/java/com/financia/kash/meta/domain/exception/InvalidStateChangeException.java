package com.financia.kash.meta.domain.exception;

import com.financia.kash.meta.domain.enums.ObjectiveState;

public class InvalidStateChangeException extends RuntimeException {

    public InvalidStateChangeException(String nameGoal, ObjectiveState status) {
        super("No se pudo cambiar el estado de la meta '" + nameGoal + "' a:" + status);
    }

}
