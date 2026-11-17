package com.financia.kash.analisis.domain.model.savingsRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsRatePeriod(
        LocalDate period,
        BigDecimal totalIncome,
        BigDecimal totalSavings,
        BigDecimal savingsRate) {

}
