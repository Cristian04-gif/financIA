package com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.CategoryType;

public record CategoryResponse(
                UUID id,
                String name,
                CategoryType type,
                LocalDate creationDate,
                boolean active) {

}
