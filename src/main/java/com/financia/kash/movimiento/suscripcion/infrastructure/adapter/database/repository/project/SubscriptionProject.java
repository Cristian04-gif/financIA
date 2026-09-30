package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;

public record SubscriptionProject(
        UUID id,
        String name,
        BigDecimal amount,
        SubscriptionFrequency frequency,
        LocalDate dateNextPayment,
        Boolean active) {

}
