package com.financia.kash.movimiento.movimiento.application.port.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;

public interface CreateMovimentUseCase {
    Motion createMotion(UUID userId, UUID accountId, UUID categoryId, TypeMovement type, BigDecimal amount,
            LocalDate date, String description, boolean common);
}
