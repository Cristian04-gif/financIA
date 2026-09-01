package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferMoneyRequest(
                @NotNull UUID idSource,
                @NotNull UUID idTarget,
                @NotNull @Positive BigDecimal amount,
                String description) {

}
