package com.financia.kash.movimiento.movimiento.application.port.input;

import java.util.UUID;

public interface ChangeStateCommonMotion {
    void deactivateCommonMovement(UUID userid, UUID movementId);

    void activateCommonMovement(UUID userid, UUID movementId);
}
