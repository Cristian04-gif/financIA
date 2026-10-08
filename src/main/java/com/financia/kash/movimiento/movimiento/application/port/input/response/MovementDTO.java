package com.financia.kash.movimiento.movimiento.application.port.input.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.cuenta.transferencia.application.port.input.response.AccountParticiped;

public record MovementDTO(
                UUID movementId,
                AccountParticiped account,
                CategoryParticiped category,
                BigDecimal amount,
                String description,
                LocalDate dateIssue,
                String receiptUrl) {

}
