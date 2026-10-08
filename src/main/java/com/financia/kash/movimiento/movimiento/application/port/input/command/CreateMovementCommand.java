package com.financia.kash.movimiento.movimiento.application.port.input.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;

public record CreateMovementCommand(UUID userId, UUID accountId, UUID categoryId, TypeMovement type, BigDecimal amount,
        LocalDate date, String description) {

}
