package com.financia.kash.movimiento.categoria.application.port.input.command;

import java.util.UUID;

public record CreateCategoryUserCommand(UUID userId, String name, String type, UUID parentCategoryId) {

}
