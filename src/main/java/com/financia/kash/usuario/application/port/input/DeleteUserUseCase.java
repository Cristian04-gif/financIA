package com.financia.kash.usuario.application.port.input;

import java.util.UUID;

public interface DeleteUserUseCase {
    void deleteMe(UUID id);
}
