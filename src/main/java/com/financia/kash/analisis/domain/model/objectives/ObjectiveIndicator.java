package com.financia.kash.analisis.domain.model.objectives;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ObjectiveIndicator(
                UUID objectiveId,
                String objectiveName,
                BigDecimal targetAmount,
                BigDecimal currentAmount,
                BigDecimal remainingAmount,
                BigDecimal progressPercentage,
                LocalDate targetDate,
                long daysRemaining,
                BigDecimal requiredMonthlyAmount,
                BigDecimal averageMonthlyContribution,
                BigDecimal monthlyDifference,
                ObjectiveProgressStatus progressStatus) {

}
