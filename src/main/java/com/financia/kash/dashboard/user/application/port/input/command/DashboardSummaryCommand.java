package com.financia.kash.dashboard.user.application.port.input.command;

import java.time.LocalDate;
import java.util.UUID;

public record DashboardSummaryCommand(UUID userId, LocalDate from, LocalDate until) {
}
