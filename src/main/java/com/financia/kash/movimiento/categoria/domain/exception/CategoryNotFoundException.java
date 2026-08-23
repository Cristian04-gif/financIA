package com.financia.kash.movimiento.categoria.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(UUID id) {
        super("La categoria con id '" + id + "' no existe");
    }

}
