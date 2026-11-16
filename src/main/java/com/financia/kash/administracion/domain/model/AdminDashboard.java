package com.financia.kash.administracion.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AdminDashboard(ReportingPeriod period, String timeZone, Instant generatedAt,
        Overview overview, List<DailyRegistrations> registrations) {
    public AdminDashboard { registrations = List.copyOf(registrations); }

    public record Overview(Users users, Categories categories, Budgets budgets, Goals goals, Ocr ocr) {}
    public record Users(long total, long active, long suspended, long inactive, long deleted,
            long pending, long administrators, long registeredInPeriod) {}
    public record Categories(long total, long active, long inactive) {}
    public record Budgets(long total, long active, long inactive, long createdInPeriod) {}
    public record Goals(long total, long completed, long startedInPeriod) {}
    public record Ocr(long successful, long failed, long total, LocalDate firstRecordedDate) {}
    public record DailyRegistrations(LocalDate date, long count) {}
}
