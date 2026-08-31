package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransferMoneyRequest(
        @NotBlank UUID idSource,
        @NotBlank UUID idTarget,
        @Positive BigDecimal amount) {

}
