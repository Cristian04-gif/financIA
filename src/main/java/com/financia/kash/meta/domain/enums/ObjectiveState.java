package com.financia.kash.meta.domain.enums;

public enum ObjectiveState {
    INICIADA,
    EN_PROCESO,
    PAUSADA,
    REANUDADA,
    COMPLETADA,
    FINALIZADA,
    CANCELADA;

    public boolean switchState(ObjectiveState newState) {
        return switch (this) {
            case INICIADA -> newState == EN_PROCESO;
            case EN_PROCESO ->
                newState == PAUSADA || newState == COMPLETADA || newState == CANCELADA || newState == FINALIZADA;
            case PAUSADA -> newState == REANUDADA;
            case REANUDADA -> newState == EN_PROCESO;
            case COMPLETADA, CANCELADA, FINALIZADA -> false;
        };
    }
}
