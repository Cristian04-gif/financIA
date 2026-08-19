package com.financia.kash.meta.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FinancialGoal {
    private final UUID id;
    private final UUID userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private GoalStatus status;
    private final LocalDate creationDate;
    private LocalDate updateDate;

    public void updateTarget(BigDecimal targetAmount, LocalDate targetDate) {
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        markAsUpdated();
    }

    public void addContribution(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.status = GoalStatus.COMPLETED;
        }
        markAsUpdated();
    }

    public BigDecimal remainingAmount() {
        return this.targetAmount.subtract(this.currentAmount);
    }

    public void cancel() {
        this.status = GoalStatus.CANCELLED;
        markAsUpdated();
    }

    private void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}
