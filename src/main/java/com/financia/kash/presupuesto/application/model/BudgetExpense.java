package com.financia.kash.presupuesto.application.model;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetExpense(UUID categoryId, BigDecimal amount) {
}

