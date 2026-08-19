package com.financia.kash.transaccion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Transaction {
    private final UUID id;
    private final UUID userId;
    private final UUID accountId;
    private final UUID categoryId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private final LocalDate creationDate;
    private LocalDate updateDate;

    public void update(BigDecimal amount, String description, LocalDate transactionDate) {
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.updateDate = LocalDate.now();
    }

    public boolean isIncome() {
        return this.type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return this.type == TransactionType.EXPENSE;
    }
}
