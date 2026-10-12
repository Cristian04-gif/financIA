package com.financia.kash.meta.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.domain.exception.InvalidContributionAmountException;
import com.financia.kash.meta.domain.exception.InvalidStateChangeException;
import com.financia.kash.meta.domain.exception.UnauthorizedContributionException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class FinancialObjectives {

    private final UUID id;
    private final UUID userId;
    private final String name;
    private String description;
    private BigDecimal currentAmount;
    private BigDecimal targetAmount;
    private Double percentageProgress;
    private final LocalDate startDate;
    private LocalDate targetDate;
    private ObjectiveState status;
    private LocalDateTime lastUpdate;

    public FinancialObjectives(UUID userId, String name, String description, BigDecimal targetAmount,
            LocalDate targetDate) {
        this.id = null;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.currentAmount = BigDecimal.ZERO;
        this.targetAmount = targetAmount;
        this.percentageProgress = 0.0;
        this.startDate = LocalDate.now();
        this.targetDate = targetDate;
        this.status = ObjectiveState.INICIADA;
        this.lastUpdate = null;
    }

    public FinancialObjectives update(String name, String description, BigDecimal targetAmount, LocalDate targetDate) {
        return new FinancialObjectives(this.id, this.userId, name, description, this.currentAmount, targetAmount,
                this.percentageProgress, this.startDate, targetDate, status, this.lastUpdate);
    }

    public void changeStatus(ObjectiveState state) {
        if (!this.status.switchState(state)) {
            throw new InvalidStateChangeException(this.name, state);
        }
        this.status = state;
    }

    public ContributeObjetive contributeObjetive(UUID movementId, BigDecimal amount,
            String description) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidContributionAmountException();
        }
        if (this.status.equals(ObjectiveState.CANCELADA) || this.status.equals(ObjectiveState.PAUSADA)
                || this.status.equals(ObjectiveState.COMPLETADA)) {
            throw new UnauthorizedContributionException(status);
        }

        this.currentAmount = this.currentAmount.add(amount);
        this.lastUpdate = LocalDateTime.now();
        calculatePercentage();
        if (this.currentAmount.equals(this.targetAmount)) {
            this.status = ObjectiveState.COMPLETADA;
        }
        return new ContributeObjetive(this.id, movementId, amount, description);
    }

    private void calculatePercentage() {
        this.percentageProgress = (this.currentAmount.divide(this.targetAmount))
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

}
