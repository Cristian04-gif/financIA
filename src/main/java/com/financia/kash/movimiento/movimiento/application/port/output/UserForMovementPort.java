package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

public interface UserForMovementPort {
    boolean isUserActive(UUID userId);
}
