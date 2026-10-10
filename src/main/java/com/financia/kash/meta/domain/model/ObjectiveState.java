package com.financia.kash.meta.domain.model;

public enum ObjectiveState {
    INICIADA,
    EN_PROCESO,
    PAUSADA,
    REANUDADA,
    COMPLETADA,
    CANCELADA;

    public boolean switchState(ObjectiveState newState) {
        return switch (this) {
            case INICIADA -> newState == EN_PROCESO;
            case EN_PROCESO -> newState == PAUSADA || newState == COMPLETADA || newState == CANCELADA;
            case PAUSADA -> newState == REANUDADA;
            case REANUDADA -> newState == EN_PROCESO;
            case COMPLETADA, CANCELADA -> false;
        };
    }
}
