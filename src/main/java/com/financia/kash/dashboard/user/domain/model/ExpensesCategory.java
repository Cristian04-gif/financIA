package com.financia.kash.dashboard.user.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record ExpensesCategory(BigDecimal totalExpenses, String currency, List<CategoriesExcenses> categories) {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoriesExcenses {
        private UUID categoryId;
        private String name;
        private BigDecimal amount;
        private Double procentage;

    }
}
