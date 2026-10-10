package com.financia.kash.meta.infrastructure.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContributionObjectiveRequest(
        @NotNull UUID accountId,
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        String description) {

}
