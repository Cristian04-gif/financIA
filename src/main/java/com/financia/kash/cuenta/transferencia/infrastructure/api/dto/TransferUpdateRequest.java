package com.financia.kash.cuenta.transferencia.infrastructure.api.dto;

import java.math.BigDecimal;

public record TransferUpdateRequest(BigDecimal newAmount, String newDescription) {

}
