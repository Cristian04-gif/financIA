package com.financia.kash.shared.application.port.output;

import java.util.Optional;
import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

public interface UserForSharedPort {
    Optional<User> findById(UUID id);
}
