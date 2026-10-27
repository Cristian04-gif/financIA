package com.financia.kash.administracion.domain.model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ReportingPeriod(LocalDate from, LocalDate to) {
    public ReportingPeriod {
        if (from == null || to == null || from.isAfter(to) || ChronoUnit.DAYS.between(from, to) >= 366) {
            throw new IllegalArgumentException("El periodo debe estar ordenado y contener entre 1 y 366 dias");
        }
    }

    public static ReportingPeriod resolve(LocalDate from, LocalDate to, Clock clock) {
        LocalDate today = LocalDate.now(clock);
        LocalDate end = to == null ? today : to;
        if (end.isAfter(today)) throw new IllegalArgumentException("El periodo no puede terminar en el futuro");
        return new ReportingPeriod(from == null ? end.withDayOfMonth(1) : from, end);
    }
}
