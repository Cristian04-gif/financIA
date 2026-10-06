package com.financia.kash.presupuesto.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.financia.kash.presupuesto.domain.model.BudgetCategory;

public record BudgetCommand(String name, LocalDate periodStart, LocalDate periodEnd,
        BigDecimal amountLimitTotal, List<BudgetCategory> categories) {
}

