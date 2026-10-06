package com.financia.kash.presupuesto.application.model;

import java.util.UUID;

public record BudgetCategoryInfo(UUID id, UUID userId, boolean expense, boolean active) {
}
