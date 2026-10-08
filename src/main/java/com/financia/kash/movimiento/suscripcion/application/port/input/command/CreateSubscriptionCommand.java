package com.financia.kash.movimiento.suscripcion.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;

public record CreateSubscriptionCommand(UUID userId, UUID payingAccountId, UUID categoryId, String name,
                BigDecimal amount,
                SubscriptionFrequency frequency,
                Integer payDay) {

}
