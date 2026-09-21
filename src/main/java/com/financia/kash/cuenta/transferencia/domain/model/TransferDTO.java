package com.financia.kash.cuenta.transferencia.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferDTO(
                UUID id,
                AccountDestination accountDestination,
                String description,
                LocalDate creationDate,
                BigDecimal amount) {

}
