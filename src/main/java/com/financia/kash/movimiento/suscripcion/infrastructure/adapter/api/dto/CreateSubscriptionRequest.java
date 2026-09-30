package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateSubscriptionRequest(
        @NotBlank UUID payingAccountId,
        @NotBlank UUID categoryId,
        @NotBlank String name,
        @NotBlank @Positive BigDecimal amount,
        @NotBlank SubscriptionFrequency frequency,
        @Positive Integer payDay) {

}
