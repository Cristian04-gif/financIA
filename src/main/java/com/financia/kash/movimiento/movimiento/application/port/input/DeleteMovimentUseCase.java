package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

public interface DeleteMovimentUseCase {
    void deleteMovement(UUID movementId);
}
