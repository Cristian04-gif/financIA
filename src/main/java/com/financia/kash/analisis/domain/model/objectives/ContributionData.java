package com.financia.kash.analisis.domain.model.objectives;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContributionData(
        UUID objectiveId,
        BigDecimal amount,
        LocalDate date) {

}
