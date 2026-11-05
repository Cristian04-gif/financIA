package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.output.FinancialDataPort;
import com.financia.kash.analisis.application.port.output.FinancialDataPort.FinancialTotals;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.analisis.domain.model.period.MetricComparison;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CompareFinancialPeriodsService {

    private final FinancialDataPort financialDataPort;

    public Mono<FinancialPeriodComparison> execute(UUID userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null || startDate == null || endDate == null) {
            return Mono.error(new IllegalArgumentException("El usuario y las fechas son obligatorias"));
        }

        if (startDate.isAfter(endDate)) {
            return Mono.error(new IllegalArgumentException("La fecha inicial no puede ser posterior a la final"));
        }

        final LocalDate previousStart;
        final LocalDate previousEnd;
        final LocalDate endExclusiveDate;

        try {
            long periodDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            previousStart = startDate.minusDays(periodDays);
            previousEnd = startDate.minusDays(1);
            endExclusiveDate = endDate.plusDays(1);
        } catch (DateTimeException e) {
            return Mono.error(new IllegalArgumentException("El rango de fechas no es válido"));
        }

        LocalDateTime currentStartInclusive = startDate.atStartOfDay();
        LocalDateTime currentEndExclusive = endExclusiveDate.atStartOfDay();
        LocalDateTime previousStartInclusive = previousStart.atStartOfDay();
        LocalDateTime previousEndExclusive = startDate.atStartOfDay();

        Mono<FinancialTotals> financialTotalsCurrentMono = financialDataPort.getTotals(userId, currentStartInclusive,
                currentEndExclusive);
        Mono<FinancialTotals> financialTotalsPreviousMono = financialDataPort.getTotals(userId, previousStartInclusive,
                previousEndExclusive);
        return Mono.zip(financialTotalsCurrentMono, financialTotalsPreviousMono).map(tuple -> {

            FinancialTotals current = tuple.getT1();
            FinancialTotals previous = tuple.getT2();

            BigDecimal currentBalance = current.income().subtract(current.expenses());
            BigDecimal previousBalance = previous.income().subtract(previous.expenses());
            BigDecimal currentUnallocated = currentBalance.subtract(current.savings());
            BigDecimal previousUnallocated = previousBalance.subtract(previous.savings());

            return new FinancialPeriodComparison(startDate, endDate, previousStart, previousEnd,
                    compare(current.income(), previous.income()), compare(current.expenses(), previous.expenses()),
                    compare(current.savings(), previous.savings()), compare(currentBalance, previousBalance),
                    compare(currentUnallocated, previousUnallocated));
        });
    }

    private MetricComparison compare(BigDecimal current, BigDecimal previous) {
        BigDecimal difference = current.subtract(previous);

        boolean hasPreviousBase = previous.compareTo(BigDecimal.ZERO) != 0;
        BigDecimal percentage = null;
        if (hasPreviousBase) {
            percentage = difference.multiply(BigDecimal.valueOf(100)).divide(previous, 2, RoundingMode.HALF_UP);
        }

        return new MetricComparison(current, previous, difference, percentage, hasPreviousBase);
    }
}
