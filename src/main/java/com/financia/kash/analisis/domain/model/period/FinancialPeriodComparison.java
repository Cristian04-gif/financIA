package com.financia.kash.analisis.domain.model.period;

import java.time.LocalDate;

public record FinancialPeriodComparison(
        LocalDate currentStartDate,
        LocalDate currentEndDate,
        LocalDate previousStartDate,
        LocalDate previousEndDate,
        MetricComparison income,
        MetricComparison expenses,
        MetricComparison savings,
        MetricComparison operatingBalance,
        MetricComparison unallocatedMoney) {

}
