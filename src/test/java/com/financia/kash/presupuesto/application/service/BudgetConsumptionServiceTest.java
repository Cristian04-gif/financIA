package com.financia.kash.presupuesto.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.financia.kash.presupuesto.application.model.BudgetExpense;
import com.financia.kash.presupuesto.application.port.input.GetBudgetUseCase;
import com.financia.kash.presupuesto.application.port.output.BudgetExpensePort;
import com.financia.kash.presupuesto.domain.exception.BudgetNotFoundException;
import com.financia.kash.presupuesto.domain.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BudgetConsumptionServiceTest {
    private final GetBudgetUseCase budgets = mock(GetBudgetUseCase.class);
    private final BudgetExpensePort expenses = mock(BudgetExpensePort.class);
    private final BudgetConsumptionService service = new BudgetConsumptionService(budgets, expenses);
    private final UUID userId = UUID.randomUUID();
    private final UUID budgetId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final LocalDate start = LocalDate.of(2026, 10, 1);
    private final LocalDate end = LocalDate.of(2026, 10, 31);

    private Budget budget() {
        return new Budget(budgetId, userId, "Octubre", start, end, new BigDecimal("100"), true,
                LocalDateTime.now(), null, List.of(new BudgetCategory(null, categoryId, new BigDecimal("40"))));
    }

    @Test
    void includesUnallocatedExpensesInTotalAndReportsCategoryExcess() {
        when(budgets.getById(userId, budgetId)).thenReturn(Mono.just(budget()));
        when(expenses.findExpensesByCategory(userId, start, end)).thenReturn(Flux.just(
                new BudgetExpense(categoryId, new BigDecimal("60.00")),
                new BudgetExpense(UUID.randomUUID(), new BigDecimal("50.00"))));
        StepVerifier.create(service.getConsumption(userId, budgetId)).assertNext(result -> {
            assertEquals(new BigDecimal("110.00"), result.spent());
            assertEquals(new BigDecimal("-10.00"), result.available());
            assertEquals(new BigDecimal("10.00"), result.exceeded());
            assertEquals(new BigDecimal("20.00"), result.categories().getFirst().exceeded());
        }).verifyComplete();
    }

    @Test
    void returnsZeroConsumptionForEmptyPeriods() {
        when(budgets.getById(userId, budgetId)).thenReturn(Mono.just(budget()));
        when(expenses.findExpensesByCategory(userId, start, end)).thenReturn(Flux.empty());
        StepVerifier.create(service.getConsumption(userId, budgetId)).assertNext(result -> {
            assertEquals(new BigDecimal("0.00"), result.spent());
            assertEquals(new BigDecimal("100.00"), result.available());
            assertEquals(new BigDecimal("0.00"), result.categories().getFirst().spent());
        }).verifyComplete();
    }

    @Test
    void doesNotReadExpensesWithoutBudgetAccess() {
        when(budgets.getById(userId, budgetId)).thenReturn(Mono.error(new BudgetNotFoundException(budgetId)));
        StepVerifier.create(service.getConsumption(userId, budgetId)).expectError(BudgetNotFoundException.class).verify();
        verifyNoInteractions(expenses);
    }
}

