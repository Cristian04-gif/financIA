package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.suscripcion.domain.enums.SubscriptionFrequency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubscriptionRequest(
                @NotNull UUID payingAccountId,
                @NotNull UUID categoryId,
                @NotNull String name,
                @NotNull @Positive BigDecimal amount,
                @NotNull SubscriptionFrequency frequency,
                @NotNull TypeMovement type,
                @Positive Integer payDay) {

}
