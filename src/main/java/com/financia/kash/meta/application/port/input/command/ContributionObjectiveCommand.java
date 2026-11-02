package com.financia.kash.meta.application.port.input.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ContributionObjectiveCommand(
        UUID objectiveId, UUID accountId, UUID categoryId,
        BigDecimal amount,
        String description) {

}
