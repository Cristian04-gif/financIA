package com.financia.kash.cuenta.cuenta.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.domain.enums.AccountType;

public record CreateAccountCommand(UUID userId, String name, AccountType type, BigDecimal initialBalance) {

}
