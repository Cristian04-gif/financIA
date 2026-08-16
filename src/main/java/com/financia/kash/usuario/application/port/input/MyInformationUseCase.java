package com.financia.kash.usuario.application.port.input;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

public interface MyInformationUseCase {
    User findMe(UUID id);

    User findMe(String email);
}
