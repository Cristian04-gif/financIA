package com.financia.kash.movimiento.movimiento.application.port.input.command;

import java.util.UUID;

public record CategoryCommand(UUID categoryId, String name, String type, UUID parentCategoryId) {
}
