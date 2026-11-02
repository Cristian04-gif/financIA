package com.financia.kash.movimiento.categoria.application.port.input.command;

import java.util.UUID;

public record UpdateCategoryCommand(UUID id, String name, String type, UUID parentId, boolean active) {

}
