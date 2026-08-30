package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.CategoryType;

public record ProjectCategory(
        UUID id,
        UUID userId,
        String name,
        CategoryType type,
        LocalDate creationDate,
        boolean active) {

}
