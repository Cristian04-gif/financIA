package com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransferUpdateRequest(
        @NotNull(message = "El nuevo monto es obligatorio")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal newAmount,
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String newDescription) {

}
