package com.financia.kash.presupuesto.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.financia.kash.presupuesto.domain.exception.BudgetValidationException;

class BudgetTest {
    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final LocalDate start = LocalDate.of(2026, 10, 1);
    private final LocalDate end = LocalDate.of(2026, 10, 31);
    private final LocalDateTime now = LocalDateTime.of(2026, 10, 6, 12, 0);

    private Budget create(BigDecimal limit, List<BudgetCategory> categories) {
        return Budget.create(userId, " Octubre ", start, end, limit, categories, now);
    }

    @Test
    void createsAnActiveBudgetWithImmutableAllocations() {
        var budget = create(new BigDecimal("100"), List.of(
                new BudgetCategory(null, categoryId, new BigDecimal("40"))));
        assertEquals("Octubre", budget.name());
        assertEquals(new BigDecimal("100.00"), budget.amountLimitTotal());
        assertTrue(budget.active());
        assertNull(budget.updateDate());
        assertThrows(UnsupportedOperationException.class, () -> budget.categories().clear());
    }

    @Test
    void rejectsInvalidPeriodsAndAllowsOneDay() {
        assertThrows(BudgetValidationException.class, () -> Budget.create(userId, "Mes",
                end, start, BigDecimal.TEN, List.of(), now));
        assertDoesNotThrow(() -> Budget.create(userId, "Dia", start, start, BigDecimal.TEN, List.of(), now));
    }

    @Test
    void rejectsDuplicateCategoriesAndExcessAllocation() {
        var category = new BudgetCategory(null, categoryId, new BigDecimal("60"));
        assertThrows(BudgetValidationException.class, () -> create(new BigDecimal("200"), List.of(category, category)));
        assertThrows(BudgetValidationException.class, () -> create(new BigDecimal("50"), List.of(category)));
        assertDoesNotThrow(() -> create(new BigDecimal("60"), List.of(category)));
    }

    @Test
    void rejectsNonPositiveOrUnrepresentableAmounts() {
        for (String value : List.of("0", "-1", "1.001", "100000000000000000")) {
            assertThrows(BudgetValidationException.class, () -> create(new BigDecimal(value), List.of()));
            assertThrows(BudgetValidationException.class, () -> new BudgetCategory(null, categoryId, new BigDecimal(value)));
        }
    }

    @Test
    void updatesPreserveIdentityAndCreationDate() {
        var original = new Budget(UUID.randomUUID(), userId, "Original", start, end,
                BigDecimal.TEN, true, now, null, List.of());
        var updated = original.update("Nuevo", start, end, new BigDecimal("20"), List.of(), now.plusDays(1));
        assertEquals(original.id(), updated.id());
        assertEquals(original.userId(), updated.userId());
        assertEquals(original.creationDate(), updated.creationDate());
        assertEquals(now.plusDays(1), updated.updateDate());
        assertFalse(updated.changeStatus(false, now.plusDays(2)).active());
    }

    @Test
    void rejectsMissingIdentityNameAndCategory() {
        assertThrows(BudgetValidationException.class, () -> Budget.create(null, "Mes",
                start, end, BigDecimal.TEN, List.of(), now));
        assertThrows(BudgetValidationException.class, () -> Budget.create(userId, " ",
                start, end, BigDecimal.TEN, List.of(), now));
        assertThrows(BudgetValidationException.class, () -> new BudgetCategory(null, null, BigDecimal.TEN));
    }
}
