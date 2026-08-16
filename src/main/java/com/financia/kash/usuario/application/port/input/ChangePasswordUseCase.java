package com.financia.kash.usuario.application.port.input;

import java.util.UUID;

public interface ChangePasswordUseCase {
    void changePassword(UUID id, String newPassword);
}
