package com.financia.kash.cuenta.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.financia.kash.transaccion.domain.model.TransactionType;

class AccountTest {

    @Test
    void applyTransactionUpdatesBalance() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .name("Cuenta principal")
                .type(AccountType.BANCO)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1000.00"))
                .creationDate(LocalDate.now())
                .active(true)
                .build();

        account.applyTransaction(new BigDecimal("250.00"), TransactionType.EXPENSE);
        account.applyTransaction(new BigDecimal("100.00"), TransactionType.INCOME);

        assertEquals(new BigDecimal("850.00"), account.getCurrentBalance());
        assertNotNull(account.getUpdateDate());
    }

    @Test
    void deactivateMarksAccountAsInactive() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .name("Efectivo")
                .type(AccountType.DINERO)
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .creationDate(LocalDate.now())
                .active(true)
                .build();

        account.deactivate();

        assertFalse(account.isActive());
        assertNotNull(account.getUpdateDate());
    }
}
