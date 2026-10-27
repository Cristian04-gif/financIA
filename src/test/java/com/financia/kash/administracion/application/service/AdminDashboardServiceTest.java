package com.financia.kash.administracion.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import org.junit.jupiter.api.Test;
import com.financia.kash.administracion.application.port.output.AdminDashboardPort;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.administracion.domain.model.AdminDashboard.*;
import reactor.core.publisher.*;

class AdminDashboardServiceTest {
    private final AdminDashboardPort port = mock(AdminDashboardPort.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-27T04:30:00Z"), ZoneId.of("America/Lima"));
    private final AdminDashboardService service = new AdminDashboardService(port, clock);
    private final Overview empty = new Overview(new Users(0,0,0,0,0,0,0,0), new Categories(0,0,0),
            new Budgets(0,0,0,0), new Goals(0,0,0), new Ocr(0,0,0,null));

    @Test
    void defaultsFollowLimaDateAndFillDaysWithoutRegistrations() {
        var period = new ReportingPeriod(LocalDate.of(2026,10,1), LocalDate.of(2026,10,26));
        when(port.overview(period)).thenReturn(Mono.just(empty));
        when(port.registrations(period)).thenReturn(Flux.just(new DailyRegistrations(LocalDate.of(2026,10,2), 3)));
        var result = service.get(null, null).block();
        assertEquals(period, result.period());
        assertEquals("America/Lima", result.timeZone());
        assertEquals(26, result.registrations().size());
        assertEquals(0, result.registrations().getFirst().count());
        assertEquals(3, result.registrations().get(1).count());
        assertEquals(LocalDate.of(2026,10,26), result.registrations().getLast().date());
        assertEquals(clock.instant(), result.generatedAt());
    }

    @Test
    void invalidPeriodsNeverQueryTheDatabase() {
        assertThrows(IllegalArgumentException.class, () -> service.get(LocalDate.of(2026,10,2), LocalDate.of(2026,10,1)).block());
        assertThrows(IllegalArgumentException.class, () -> service.get(null, LocalDate.of(2026,10,27)).block());
        assertThrows(IllegalArgumentException.class, () -> service.get(LocalDate.of(2025,1,1), LocalDate.of(2026,10,1)).block());
        verifyNoInteractions(port);
    }

    @Test
    void leapYearAndSingleDayRangesAreInclusive() {
        var leapYear = new ReportingPeriod(LocalDate.of(2024,1,1), LocalDate.of(2024,12,31));
        assertEquals(366, leapYear.from().datesUntil(leapYear.to().plusDays(1)).count());
        assertThrows(IllegalArgumentException.class, () -> new ReportingPeriod(LocalDate.of(2024,1,1), LocalDate.of(2025,1,1)));
        var day = LocalDate.of(2026,10,1);
        when(port.overview(new ReportingPeriod(day, day))).thenReturn(Mono.just(empty));
        when(port.registrations(new ReportingPeriod(day, day))).thenReturn(Flux.empty());
        assertEquals(1, service.get(day, day).block().registrations().size());
    }
}
