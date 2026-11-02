package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import jakarta.validation.constraints.NotNull;

public record BudgetStatusRequest(@NotNull Boolean active) {
}
