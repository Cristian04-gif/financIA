package com.financia.kash.movimiento.movimiento.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;

public record CreateMovementCommand(UUID userId, String username, UUID accountId, CategoryCommand category,
        TypeMovement type,
        BigDecimal amount,
        String description) {

}
