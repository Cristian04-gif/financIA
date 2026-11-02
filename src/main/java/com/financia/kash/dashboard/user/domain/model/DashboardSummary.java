package com.financia.kash.dashboard.user.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DashboardSummary {
    private Period period;
    private DetailedFinancialSummary detailedFinancialSummary;
    private ComparisonPreviousPeriod comparisonPreviousPeriod;
    private BudgetDashboard budget;
    private Goals goals;

    public DashboardSummary(Period period, DetailedFinancialSummary detailedFinancialSummary,
            ComparisonPreviousPeriod comparisonPreviousPeriod, BudgetDashboard budget, Goals goals) {
        this.period = period;
        this.detailedFinancialSummary = detailedFinancialSummary;
        this.comparisonPreviousPeriod = comparisonPreviousPeriod;
        this.budget = budget;
        this.goals = goals;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Period {
        private LocalDate from;
        private LocalDate until;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedFinancialSummary {
        private BigDecimal totalBalance;
        private BigDecimal income;
        private BigDecimal costs;
        private BigDecimal periodBalance;
        private String currency;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComparisonPreviousPeriod {
        private Double revenueVariationPercentage;
        private Double variationExpensesPercentage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BudgetDashboard {
        private BigDecimal totalAmount;
        private BigDecimal amountSpent;
        private BigDecimal availableAmount;
        private Double percentageConsumed;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goals {
        private int active;
        private int completed;
        private Double totalProgressPercentage;
    }
}
