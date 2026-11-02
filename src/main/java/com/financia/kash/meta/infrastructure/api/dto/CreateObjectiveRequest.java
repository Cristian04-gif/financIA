package com.financia.kash.meta.infrastructure.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateObjectiveRequest(
        @NotNull String name,
        String description,
        @NotNull @Positive BigDecimal targetAmount,
        @NotNull @Future LocalDate targetDate) {

}
