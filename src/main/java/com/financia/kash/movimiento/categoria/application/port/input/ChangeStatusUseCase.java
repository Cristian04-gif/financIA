package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

public interface ChangeStatusUseCase {
    void changeStatus(UUID id);
}
