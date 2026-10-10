package com.financia.kash.meta.infrastructure.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContributionProject(
                UUID id,
                BigDecimal amount,
                LocalDate date,
                String description) {

}
