package com.financia.kash.meta.application.port.input.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateObjectiveCommand(UUID userId, String name, String description, BigDecimal targetAmount,
                LocalDate targetDate) {

}
