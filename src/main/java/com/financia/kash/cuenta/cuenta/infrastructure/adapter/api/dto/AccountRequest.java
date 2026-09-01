package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto;

import java.math.BigDecimal;

import com.financia.kash.cuenta.cuenta.domain.model.AccountType;
import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountRequest(
        @CharactersOnly @Size(min = 3) String name,
        @NotNull AccountType type,
        @DecimalMin("50.0") BigDecimal initialBalance) {

}
