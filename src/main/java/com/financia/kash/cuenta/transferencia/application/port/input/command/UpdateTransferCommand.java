package com.financia.kash.cuenta.transferencia.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateTransferCommand(UUID transferId, BigDecimal newAmount, String newDescription) {

}
