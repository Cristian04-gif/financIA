package com.financia.kash.cuenta.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.transaccion.domain.model.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Account {
    private final UUID id;
    private final UUID userId;
    private String name;
    private AccountType type;
    private final BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private final LocalDate creationDate;
    private LocalDate updateDate;
    private boolean active;

    public void rename(String name) {
        this.name = name;
        markAsUpdated();
    }

    public void changeType(AccountType type) {
        this.type = type;
        markAsUpdated();
    }

    public void applyTransaction(BigDecimal amount, TransactionType transactionType) {
        if (transactionType == TransactionType.INCOME) {
            this.currentBalance = this.currentBalance.add(amount);
        } else {
            this.currentBalance = this.currentBalance.subtract(amount);
        }
        markAsUpdated();
    }

    public void deactivate() {
        this.active = false;
        markAsUpdated();
    }

    private void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}
