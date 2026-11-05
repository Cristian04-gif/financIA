package com.financia.kash.movimiento.movimiento.application.port.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.cuenta.transferencia.application.port.input.response.AccountParticiped;

public record MovementDTO(
                UUID movementId,
                AccountParticiped account,
                CategoryParticiped category,
                BigDecimal amount,
                String description,
                LocalDateTime dateIssue,
                String receiptUrl) {

}
