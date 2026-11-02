package com.financia.kash.meta.infrastructure.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.meta.domain.enums.ObjectiveState;

public record ContributionProject(
                UUID id,
                UUID objective_id,
                String name_objective,
                String description_objective,
                BigDecimal current_amount,
                BigDecimal target_amount,
                Double percentage_progress,
                ObjectiveState status,
                BigDecimal amount,
                LocalDate date,
                String description) {

}
