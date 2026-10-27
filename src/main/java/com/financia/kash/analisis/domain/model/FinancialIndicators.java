package com.financia.kash.analisis.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FinancialIndicators(
                LocalDate startDate,
                LocalDate endDate,
                BigDecimal income,
                BigDecimal expenses,
                BigDecimal pperatingBalance,
                BigDecimal recordedSavings,
                BigDecimal unallocatedFunds,
                List<ExpenseByCategory> expensesByCategory) {
}
