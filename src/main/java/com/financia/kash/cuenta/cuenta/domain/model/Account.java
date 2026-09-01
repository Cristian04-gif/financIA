package com.financia.kash.cuenta.cuenta.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.exception.InactiveAccountException;
import com.financia.kash.cuenta.cuenta.domain.exception.InsufficientFundsException;
import com.financia.kash.cuenta.cuenta.domain.exception.InvalidAmountException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
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

    public Account(UUID userId, String name, AccountType type, BigDecimal initialBalance) {
        this.id = null;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.currentBalance = initialBalance;
        this.creationDate = LocalDate.now();
        this.updateDate = null;
        this.active = true;
    }

    public void validateAccountIsActive() {
        if (!this.active) {
            throw new InactiveAccountException(this.name);
        }
    }

    public void validateSufficientFunds(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El importe de la transferencia debe ser positivo");
        }
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(this.name);
        }
    }

    public void transfer(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
        this.currentBalance = this.currentBalance.subtract(amount);
        this.updateDate = LocalDate.now();
    }

    public void receive(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
        this.updateDate = LocalDate.now();
    }

    public void changeStatus() {
        this.active = !this.active;
    }
}
