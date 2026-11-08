package com.financia.kash.analisis.domain.model.objectives;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ObjectiveData(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate startDate,
        LocalDate targetDate) {

}
