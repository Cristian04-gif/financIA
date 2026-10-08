package com.financia.kash.cuenta.transferencia.application.port.input.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferDTO(
                UUID id,
                AccountParticiped accountOrigin,
                AccountParticiped accountDestination,
                String description,
                LocalDate creationDate,
                BigDecimal amount) {

}
