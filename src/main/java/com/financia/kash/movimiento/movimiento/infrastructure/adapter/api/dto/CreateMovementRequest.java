package com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovementRequest(
                @NotNull UUID accountId,
                @NotNull UUID categoryId,
                @NotNull TypeMovement type,
                @NotNull @Positive BigDecimal amount,
                @NotNull LocalDate date, String description) {

}
