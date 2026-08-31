package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.model.AccountType;
import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountRequest(
        @NotBlank UUID userId,
        @CharactersOnly @Size(min = 3) String name,
        @NotBlank AccountType type,
        @DecimalMin("50.0") BigDecimal initialBalance) {

}
