package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto;

import java.math.BigDecimal;

import com.financia.kash.cuenta.cuenta.domain.enums.AccountType;
import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountRequest(
                @NotBlank @CharactersOnly @Size(min = 3) String name,
                @NotNull AccountType type,
                @NotNull @DecimalMin("50.0") BigDecimal initialBalance) {

}
