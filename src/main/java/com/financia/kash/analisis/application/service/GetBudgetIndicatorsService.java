package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.output.BudgetDataPort;
import com.financia.kash.analisis.domain.model.budgetConsumtion.BudgetConsumptionStatus;
import com.financia.kash.analisis.domain.model.budgetConsumtion.BudgetIndicators;
import com.financia.kash.analisis.domain.model.budgetConsumtion.CategoryBudgetIndicator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetBudgetIndicatorsService {

    private static final BigDecimal WARNING_THRESHOLD = new BigDecimal("80");
    private final BudgetDataPort budgetDataPort;

    public Mono<BudgetIndicators> execute(UUID budgetId, UUID userId) {
        if (budgetId == null || userId == null) {
            return Mono.error(new IllegalArgumentException("El presupuesto y el usuario son obligatorios"));
        }

        return budgetDataPort.findByIdAndUserId(budgetId, userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El presupuesto no existe")))
                .flatMap(budget -> {

                    Mono<BigDecimal> totalSpentMono = budgetDataPort.getTotalSpent(userId, budget.periodStart(),
                            budget.periodEnd());

                    return Mono.zip(totalSpentMono,
                            budgetDataPort
                                    .getCategorySpending(budget.id(), userId, budget.periodStart(), budget.periodEnd())
                                    .collectList())
                            .map(tuple -> {
                                BigDecimal totalSpent = tuple.getT1();
                                BigDecimal totalAvailable = budget.totalLimit().subtract(totalSpent);
                                BigDecimal totalPercentage = calculatePercentage(totalSpent, budget.totalLimit());

                                var categories = tuple.getT2().stream().map(this::mapCategory).toList();

                                return new BudgetIndicators(budget.id(),
                                        budget.name(),
                                        budget.periodStart(),
                                        budget.periodEnd(),
                                        budget.totalLimit(),
                                        totalSpent,
                                        totalAvailable,
                                        totalPercentage,
                                        determineStatus(totalPercentage),
                                        categories);
                            });
                });
    }

    private CategoryBudgetIndicator mapCategory(BudgetDataPort.CategoryBudgetData category) {
        BigDecimal available = category.limit().subtract(category.spent());

        BigDecimal percentage = calculatePercentage(category.spent(), category.limit());

        return new CategoryBudgetIndicator(category.categoryId(), category.categoryName(), category.limit(),
                category.spent(), available, percentage, determineStatus(percentage));
    }

    private BigDecimal calculatePercentage(BigDecimal spent, BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return spent.multiply(BigDecimal.valueOf(100)).divide(limit, 2, RoundingMode.HALF_UP);
    }

    private BudgetConsumptionStatus determineStatus(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BudgetConsumptionStatus.SUPERADO;
        }

        if (percentage.compareTo(WARNING_THRESHOLD) >= 0) {
            return BudgetConsumptionStatus.CERCA_DEL_LIMITE;
        }

        return BudgetConsumptionStatus.DENTRO_DEL_LIMITE;
    }
}
