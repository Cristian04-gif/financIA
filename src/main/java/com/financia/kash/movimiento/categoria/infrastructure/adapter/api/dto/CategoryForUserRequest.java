package com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto;

import java.util.UUID;

public record CategoryForUserRequest(String name, String type, UUID parentCategoryId, boolean active) {

}
