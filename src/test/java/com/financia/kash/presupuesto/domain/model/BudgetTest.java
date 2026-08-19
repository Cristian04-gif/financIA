package com.financia.kash.presupuesto.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class BudgetTest {

    @Test
    void calculatesRemainingAmountAndExceededStatus() {
        Budget budget = Budget.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .name("Comida")
                .limitAmount(new BigDecimal("500.00"))
                .spentAmount(new BigDecimal("0.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .creationDate(LocalDate.now())
                .active(true)
                .build();

        budget.addExpense(new BigDecimal("300.00"));

        assertEquals(new BigDecimal("200.00"), budget.remainingAmount());
        assertFalse(budget.isExceeded());

        budget.addExpense(new BigDecimal("250.00"));

        assertEquals(new BigDecimal("-50.00"), budget.remainingAmount());
        assertTrue(budget.isExceeded());
    }
}
