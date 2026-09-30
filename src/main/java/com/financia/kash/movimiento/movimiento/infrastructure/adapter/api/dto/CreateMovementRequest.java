package com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateMovementRequest(
                @NotBlank UUID accountId,
                @NotBlank UUID categoryId,
                @NotBlank TypeMovement type,
                @NotBlank @Positive BigDecimal amount,
                @NotBlank LocalDate date, String description) {

}
