package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.AccountType;

public record AccountProject(
        UUID id,
        String name,
        AccountType type,
        BigDecimal currentBalance,
        LocalDate creationDate,
        boolean active) {
}
