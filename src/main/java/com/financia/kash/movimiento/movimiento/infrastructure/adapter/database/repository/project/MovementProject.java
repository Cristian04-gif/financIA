package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovementProject(
                UUID movement_id,
                UUID account_id,
                String account_name,
                UUID category_id,
                String category_name,
                String category_type,
                BigDecimal amount,
                String description,
                LocalDate date_issue) {

}
