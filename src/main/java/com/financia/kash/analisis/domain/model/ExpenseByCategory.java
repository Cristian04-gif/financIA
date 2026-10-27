package com.financia.kash.analisis.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseByCategory(
        UUID categoryId,
        String categoryName,
        BigDecimal amount) {
}
