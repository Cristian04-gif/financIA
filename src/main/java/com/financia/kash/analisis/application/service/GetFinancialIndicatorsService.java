package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.output.FinancialDataPort;
import com.financia.kash.analisis.application.port.output.FinancialDataPort.FinancialTotals;
import com.financia.kash.analisis.domain.model.ExpenseByCategory;
import com.financia.kash.analisis.domain.model.FinancialIndicators;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetFinancialIndicatorsService {

    private final FinancialDataPort financialDataPort;

    public Mono<FinancialIndicators> execute(UUID userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null || startDate == null || endDate == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "El usuario y las fechas son obligatorios"));
        }
        if (startDate.isAfter(endDate)) {
            return Mono.error(new IllegalArgumentException("La fecha inicial no puede ser posterior a la final"));
        }

        var startInclusive = startDate.atStartOfDay();
        var endExclusive = endDate.plusDays(1).atStartOfDay();

        Mono<FinancialTotals> financialTotalsMono = financialDataPort.getTotals(userId, startInclusive, endExclusive);

        Flux<ExpenseByCategory> expenseByCategoryFlux = financialDataPort.getExpensesByCategory(userId, startInclusive,
                endExclusive);

        return Mono
                .zip(financialTotalsMono, expenseByCategoryFlux.collectList())
                .map(tuple -> {
                    FinancialTotals totals = tuple.getT1();
                    List<ExpenseByCategory> expensesByCategory = tuple.getT2();

                    BigDecimal balanceOperativo = totals.income().subtract(totals.expenses());
                    BigDecimal dineroNoAsignado = balanceOperativo.subtract(totals.savings());
                    return new FinancialIndicators(startDate, endDate, totals.income(), totals.expenses(),
                            balanceOperativo, totals.savings(), dineroNoAsignado, expensesByCategory);
                });
    }
}
