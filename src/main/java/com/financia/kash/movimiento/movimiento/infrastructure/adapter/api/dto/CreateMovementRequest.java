package com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.application.port.input.command.CategoryCommand;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovementRequest(
                @NotNull UUID accountId,
                @NotNull CategoryCommand category,
                @NotNull TypeMovement type,
                @NotNull @Positive BigDecimal amount,
                String description) {

}
