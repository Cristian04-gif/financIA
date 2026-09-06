package com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.dto;

import java.math.BigDecimal;

public record TransferUpdateRequest(BigDecimal newAmount, String newDescription) {

}
