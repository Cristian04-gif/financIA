package com.financia.kash.usuario.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

public interface UserRepositoryPort {
    User getMe(UUID id);

    User getMe(String email);

    boolean existEmail(String email);

    User save(User user);

    void delete(UUID id);

}
