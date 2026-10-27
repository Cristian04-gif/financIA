package com.financia.kash.administracion.infrastructure.api;

import static org.mockito.Mockito.*;
import java.time.*;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.administracion.application.port.output.AdminDashboardPort;
import com.financia.kash.administracion.application.service.AdminDashboardService;
import com.financia.kash.administracion.domain.model.AdminDashboard.*;
import com.financia.kash.auth.application.service.JwtService;
import com.financia.kash.auth.infrastructure.security.config.*;
import com.financia.kash.auth.infrastructure.security.filters.JwtFilter;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.AccountAuthorizationManager;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.TransferAuthorizationManager;
import com.financia.kash.meta.infrastructure.api.contribution.ContributionObjectiveAuthorizationManager;
import com.financia.kash.meta.infrastructure.api.objective.ObjectiveAuthorizationManager;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.CategoryAuthorizationManager;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.MovementAuthorizationManager;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api.SubscriptionAuthorizationManager;
import com.financia.kash.usuario.domain.enums.*;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import reactor.core.publisher.*;

class AdminDashboardControllerTest {
    private AnnotationConfigApplicationContext context;
    private WebTestClient client;
    private AdminDashboardPort metrics;
    private UserEntity user;

    @BeforeEach
    void setup() {
        context = new AnnotationConfigApplicationContext();
        org.springframework.boot.test.util.TestPropertyValues.of("security.secret-key=" + Base64.getEncoder()
                .encodeToString("admin-dashboard-test-secret-key-12345678901234567890".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .applyTo(context);
        metrics = mock(AdminDashboardPort.class);
        var users = mock(ReactiveUserDetailsService.class);
        context.registerBean(AdminDashboardPort.class, () -> metrics);
        context.registerBean(ReactiveUserDetailsService.class, () -> users);
        context.registerBean(Clock.class, () -> Clock.fixed(Instant.parse("2026-10-27T12:00:00Z"), ZoneId.of("America/Lima")));
        context.registerBean(ObjectMapper.class, () -> new ObjectMapper());
        context.registerBean("jwtFilter", JwtFilter.class, () -> new JwtFilter(context.getBean(JwtService.class), users));
        mockBean(AccountAuthorizationManager.class);
        mockBean(TransferAuthorizationManager.class);
        mockBean(CategoryAuthorizationManager.class);
        mockBean(MovementAuthorizationManager.class);
        mockBean(SubscriptionAuthorizationManager.class);
        mockBean(ObjectiveAuthorizationManager.class);
        mockBean(ContributionObjectiveAuthorizationManager.class);
        context.register(TestConfiguration.class);
        context.refresh();
        user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@example.com");
        user.setRole(RoleUser.ADMIN);
        user.setStatus(UserStatus.ACTIVO);
        when(users.findByUsername(user.getEmail())).thenAnswer(invocation -> Mono.just(user));
        String token = context.getBean(JwtService.class).generateToken(user);
        client = WebTestClient.bindToApplicationContext(context).configureClient()
                .defaultHeader("Authorization", "Bearer " + token).build();
    }

    private <T> void mockBean(Class<T> type) { context.registerBean(type, () -> mock(type)); }

    @AfterEach
    void close() { context.close(); }

    @Test
    void productionSecurityRejectsAnonymousRegularAndSuspendedUsers() {
        WebTestClient.bindToApplicationContext(context).build().get().uri("/api/v1/admin/dashboard")
                .exchange().expectStatus().isUnauthorized();
        user.setRole(RoleUser.USER);
        client.get().uri("/api/v1/admin/dashboard").exchange().expectStatus().isForbidden();
        user.setRole(RoleUser.ADMIN);
        user.setStatus(UserStatus.SUSPENDIDO);
        client.get().uri("/api/v1/admin/dashboard").exchange().expectStatus().isUnauthorized();
        verifyNoInteractions(metrics);
    }

    @Test
    void activeAdministratorReceivesAggregatesAndInclusiveSeries() {
        when(metrics.overview(any())).thenReturn(Mono.just(new Overview(new Users(5,3,1,1,0,0,1,2),
                new Categories(2,1,1), new Budgets(4,3,1,2), new Goals(3,1,2), new Ocr(8,2,10,null))));
        when(metrics.registrations(any())).thenReturn(Flux.just(new DailyRegistrations(LocalDate.of(2026,10,2),2)));
        client.get().uri("/api/v1/admin/dashboard?from=2026-10-01&to=2026-10-03").exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.period.from").isEqualTo("2026-10-01")
                .jsonPath("$.period.to").isEqualTo("2026-10-03")
                .jsonPath("$.timeZone").isEqualTo("America/Lima")
                .jsonPath("$.overview.users.total").isEqualTo(5)
                .jsonPath("$.overview.ocr.total").isEqualTo(10)
                .jsonPath("$.registrations.length()").isEqualTo(3)
                .jsonPath("$.registrations[0].count").isEqualTo(0)
                .jsonPath("$.registrations[1].count").isEqualTo(2)
                .jsonPath("$.overview.users.email").doesNotExist()
                .jsonPath("$.overview.users.password").doesNotExist();
    }

    @Test
    void invalidDatesAndRangesReturn400BeforeReadingMetrics() {
        for (String query : new String[]{"from=invalid", "from=2026-02-30", "from=2026-10-03&to=2026-10-01",
                "to=2026-10-28", "from=2025-01-01&to=2026-10-01"}) {
            client.get().uri("/api/v1/admin/dashboard?" + query).exchange().expectStatus().isBadRequest();
        }
        verifyNoInteractions(metrics);
    }

    @Configuration
    @EnableWebFlux
    @Import({SecurityConfig.class, JwtService.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class,
            AdminDashboardController.class, AdminDashboardService.class, AdminExceptionHandler.class})
    static class TestConfiguration {}
}
