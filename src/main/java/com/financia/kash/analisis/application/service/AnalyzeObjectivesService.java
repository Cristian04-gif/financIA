package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeObjectivesUseCase;
import com.financia.kash.analisis.application.port.output.ObjectiveAnalysisPort;
import com.financia.kash.analisis.domain.model.objectives.ContributionData;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveData;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicator;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveProgressStatus;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnalyzeObjectivesService implements AnalyzeObjectivesUseCase {

    private final ObjectiveAnalysisPort objectiveAnalysisPort;
    private final Clock clock = Clock.systemDefaultZone();
    private static final BigDecimal DAYS_PER_MONTH = BigDecimal.valueOf(30);

    @Override
    public Mono<ObjectiveIndicators> analyze(UUID userId) {
        return objectiveAnalysisPort.findObjectives(userId)
                .flatMap(objective -> objectiveAnalysisPort.findContributions(userId, objective.id()).collectList()
                        .map(contributions -> analyzeObjective(objective, contributions)))
                .collectList().map(this::buildSummary);
    }

    private ObjectiveIndicators buildSummary(List<ObjectiveIndicator> objectives) {
        int total = objectives.size();

        int completed = (int) objectives.stream()
                .filter(objective -> objective.progressStatus() == ObjectiveProgressStatus.COMPLETADA).count();

        int overdue = (int) objectives.stream()
                .filter(objective -> objective.progressStatus() == ObjectiveProgressStatus.VENCIDA).count();

        int active = total - completed - overdue;

        return new ObjectiveIndicators(total, completed, active, overdue, objectives);
    }

    private ObjectiveIndicator analyzeObjective(ObjectiveData objective, List<ContributionData> contributions) {
        LocalDate today = LocalDate.now(clock);

        BigDecimal targetAmount = objective.targetAmount();
        BigDecimal currentAmount = objective.currentAmount();

        BigDecimal remainingAmount = targetAmount.subtract(currentAmount).max(BigDecimal.ZERO);

        BigDecimal progressPercentage = calculateProgress(currentAmount, targetAmount);

        long daysRemaining = ChronoUnit.DAYS.between(today, objective.targetDate());

        BigDecimal requiredMonthlyAmount = calculaterequiredMonthlyAmount(remainingAmount, daysRemaining);

        BigDecimal averageMonthlyContribution = calculateAverageMonthlyContribution(contributions);

        BigDecimal monthlyDifference = averageMonthlyContribution.subtract(requiredMonthlyAmount);

        ObjectiveProgressStatus status = determineStatus(currentAmount, targetAmount, objective.targetDate(),
                daysRemaining, averageMonthlyContribution, requiredMonthlyAmount);

        return new ObjectiveIndicator(objective.id(),
                objective.name(),
                targetAmount,
                currentAmount,
                remainingAmount,
                progressPercentage,
                objective.targetDate(),
                daysRemaining,
                requiredMonthlyAmount,
                averageMonthlyContribution,
                monthlyDifference,
                status);
    }

    private ObjectiveProgressStatus determineStatus(BigDecimal currentAmount, BigDecimal targetAmount,
            LocalDate targetDate, long daysRemaining, BigDecimal averageMonthlyContribution,
            BigDecimal requiredMonthlyAmount) {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            return ObjectiveProgressStatus.COMPLETADA;
        }

        if (daysRemaining < 0) {
            return ObjectiveProgressStatus.VENCIDA;
        }

        if (averageMonthlyContribution.compareTo(
                requiredMonthlyAmount) >= 0) {
            return ObjectiveProgressStatus.EN_RITMO;
        }

        return ObjectiveProgressStatus.RITMO_INSUFICIENTE;
    }

    private BigDecimal calculateAverageMonthlyContribution(List<ContributionData> contributions) {
        if (contributions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<YearMonth, BigDecimal> monthlyContributions = contributions.stream()
                .collect(Collectors.groupingBy(contribution -> YearMonth.from(contribution.date()),
                        Collectors.reducing(BigDecimal.ZERO, ContributionData::amount, BigDecimal::add)));

        return monthlyContributions.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(monthlyContributions.size()));
    }

    private BigDecimal calculaterequiredMonthlyAmount(BigDecimal remainingAmount, long daysRemaining) {
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (daysRemaining <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthsRemaining = BigDecimal.valueOf(daysRemaining).divide(DAYS_PER_MONTH, 4, RoundingMode.HALF_UP);

        return remainingAmount.divide(monthsRemaining, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateProgress(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2,
                RoundingMode.HALF_UP);
    }
}
