package com.financia.kash.meta.application.port.input.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContributionDTO(UUID id,
        BigDecimal amount,
        LocalDate date,
        String description) {

}
