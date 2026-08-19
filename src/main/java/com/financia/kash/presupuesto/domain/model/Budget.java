package com.financia.kash.presupuesto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Budget {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private String name;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private final LocalDate creationDate;
    private LocalDate updateDate;
    private boolean active;

    public void updateLimit(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
        markAsUpdated();
    }

    public void addExpense(BigDecimal amount) {
        this.spentAmount = this.spentAmount.add(amount);
        markAsUpdated();
    }

    public BigDecimal remainingAmount() {
        return this.limitAmount.subtract(this.spentAmount);
    }

    public boolean isExceeded() {
        return this.spentAmount.compareTo(this.limitAmount) > 0;
    }

    public void deactivate() {
        this.active = false;
        markAsUpdated();
    }

    private void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}
