package com.financia.kash.movimiento.categoria.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

public interface UserForCategoryPort {
    User findUserById(UUID userI);
}
