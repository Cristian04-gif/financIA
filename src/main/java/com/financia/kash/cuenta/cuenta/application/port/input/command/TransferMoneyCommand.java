package com.financia.kash.cuenta.cuenta.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferMoneyCommand(UUID idSource, UUID idTarget, BigDecimal amount, String description) {

}
