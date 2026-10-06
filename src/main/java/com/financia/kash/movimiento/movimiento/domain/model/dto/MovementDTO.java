package com.financia.kash.movimiento.movimiento.domain.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.cuenta.transferencia.domain.model.AccountParticiped;

public record MovementDTO(
                UUID movementId,
                AccountParticiped account,
                CategoryParticiped category,
                BigDecimal amount,
                String description,
                LocalDate dateIssue,
                String receiptUrl) {

}
