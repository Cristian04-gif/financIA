package com.financia.kash.presupuesto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.financia.kash.presupuesto.domain.exception.BudgetValidationException;

public record Budget(
        UUID id, UUID userId, String name,
        LocalDate periodStart, LocalDate periodEnd,
        BigDecimal amountLimitTotal, boolean active,
        LocalDateTime creationDate, LocalDateTime updateDate,
        List<BudgetCategory> categories) {

    public Budget {
        if (userId == null) {
            throw new BudgetValidationException("El propietario es obligatorio");
        }
        if (name == null || name.isBlank() || name.strip().length() > 255) {
            throw new BudgetValidationException("El nombre es obligatorio y admite hasta 255 caracteres");
        }
        name = name.strip();
        if (periodStart == null || periodEnd == null || periodStart.isAfter(periodEnd)) {
            throw new BudgetValidationException("El periodo inicial debe ser anterior o igual al final");
        }
        amountLimitTotal = BudgetAmounts.positive(amountLimitTotal);
        if (creationDate == null || categories == null || categories.stream().anyMatch(c -> c == null)) {
            throw new BudgetValidationException("Las fechas de auditoria y la lista de categorias son obligatorias");
        }
        categories = List.copyOf(categories);
        var categoryIds = new HashSet<UUID>();
        BigDecimal allocated = BigDecimal.ZERO;
        for (BudgetCategory category : categories) {
            if (!categoryIds.add(category.categoryId())) {
                throw new BudgetValidationException("Una categoria no puede repetirse dentro del presupuesto");
            }
            allocated = allocated.add(category.amountLimit());
        }
        if (allocated.compareTo(amountLimitTotal) > 0) {
            throw new BudgetValidationException("La suma de limites por categoria supera el limite total");
        }
    }

    public static Budget create(UUID userId, String name, LocalDate start, LocalDate end,
            BigDecimal limit, List<BudgetCategory> categories, LocalDateTime now) {
        return new Budget(null, userId, name, start, end, limit, true, now, null, categories);
    }

    public Budget update(String name, LocalDate start, LocalDate end, BigDecimal limit,
            List<BudgetCategory> categories, LocalDateTime now) {
        return new Budget(id, userId, name, start, end, limit, active, creationDate, now, categories);
    }

    public Budget changeStatus(boolean active, LocalDateTime now) {
        return new Budget(id, userId, name, periodStart, periodEnd, amountLimitTotal,
                active, creationDate, now, categories);
    }
}
