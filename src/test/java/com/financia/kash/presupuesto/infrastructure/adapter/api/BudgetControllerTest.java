package com.financia.kash.presupuesto.infrastructure.adapter.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;

import com.financia.kash.auth.application.service.JwtService;
import com.financia.kash.auth.infrastructure.security.config.*;
import com.financia.kash.auth.infrastructure.security.filters.JwtFilter;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.AccountAuthorizationManager;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.TransferAuthorizationManager;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.CategoryAuthorizationManager;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.MovementAuthorizationManager;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api.SubscriptionAuthorizationManager;
import com.financia.kash.presupuesto.application.model.*;
import com.financia.kash.presupuesto.application.service.*;
import com.financia.kash.presupuesto.domain.exception.*;
import com.financia.kash.presupuesto.domain.model.*;
import com.financia.kash.presupuesto.infrastructure.adapter.api.dto.*;
import com.financia.kash.presupuesto.infrastructure.adapter.api.mapping.BudgetApiMapperImpl;
import com.financia.kash.usuario.domain.model.*;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

class BudgetControllerTest {
    private AnnotationConfigApplicationContext context;
    private WebTestClient client;
    private BudgetService budgets;
    private BudgetConsumptionService consumption;
    private UUID userId;
    private UUID categoryId;
    private UUID budgetId;

    @BeforeEach
    void setup() {
        context = new AnnotationConfigApplicationContext(TestConfiguration.class);
        budgets = context.getBean(BudgetService.class);
        consumption = context.getBean(BudgetConsumptionService.class);
        userId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        budgetId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEmail("budget@example.com");
        user.setRole(RoleUser.USER);
        user.setStatus(EstadoUsuario.ACTIVO);
        when(context.getBean(ReactiveUserDetailsService.class).findByUsername(user.getEmail()))
                .thenReturn(Mono.just(user));
        String token = context.getBean(JwtService.class).generateToken(user);
        client = WebTestClient.bindToApplicationContext(context).configureClient()
                .defaultHeader("Authorization", "Bearer " + token).build();
    }

    @AfterEach
    void close() {
        context.close();
    }

    private Budget budget() {
        return new Budget(budgetId, userId, "Octubre", LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), new BigDecimal("100"), true,
                LocalDateTime.of(2026, 10, 6, 12, 0), null,
                List.of(new BudgetCategory(UUID.randomUUID(), categoryId, new BigDecimal("40"))));
    }

    private BudgetRequest request() {
        return new BudgetRequest("Octubre", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                new BigDecimal("100"), List.of(new BudgetCategoryRequest(categoryId, new BigDecimal("40"))));
    }

    @Test
    void requiresAuthenticationUsingTheProductionSecurityChain() {
        WebTestClient.bindToApplicationContext(context).build().get().uri("/api/v1/budgets")
                .exchange().expectStatus().isUnauthorized();
        verifyNoInteractions(budgets);
    }

    @Test
    void createsBudgetUsingTheAuthenticatedIdentity() {
        when(budgets.create(eq(userId), any(BudgetCommand.class))).thenReturn(Mono.just(budget()));
        client.post().uri("/api/v1/budgets").bodyValue(request()).exchange()
                .expectStatus().isCreated().expectHeader().valueEquals("Location", "/api/v1/budgets/" + budgetId)
                .expectBody().jsonPath("$.id").isEqualTo(budgetId.toString())
                .jsonPath("$.userId").isEqualTo(userId.toString())
                .jsonPath("$.categories[0].categoryId").isEqualTo(categoryId.toString());
    }

    @Test
    void validatesNestedCategoryIdsAndAmountsBeforeCallingTheUseCase() {
        var invalid = new BudgetRequest("Octubre", request().periodStart(), request().periodEnd(),
                new BigDecimal("100"), List.of(new BudgetCategoryRequest(null, new BigDecimal("-1"))));
        client.post().uri("/api/v1/budgets").bodyValue(invalid).exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errors").exists();
        verifyNoInteractions(budgets);
    }

    @Test
    void mapsDomainErrorsAndHidesForeignBudgets() {
        when(budgets.getById(userId, budgetId)).thenReturn(Mono.error(new BudgetNotFoundException(budgetId)));
        client.get().uri("/api/v1/budgets/" + budgetId).exchange().expectStatus().isNotFound();
        when(budgets.create(eq(userId), any())).thenReturn(Mono.error(new BudgetValidationException("Periodo invalido")));
        client.post().uri("/api/v1/budgets").bodyValue(request()).exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").isEqualTo("Periodo invalido");
    }

    @Test
    void listsReadsAndReplacesBudgets() {
        when(budgets.getAll(userId)).thenReturn(Flux.just(budget()));
        when(budgets.getById(userId, budgetId)).thenReturn(Mono.just(budget()));
        when(budgets.update(eq(userId), eq(budgetId), any())).thenReturn(Mono.just(budget()));
        client.get().uri("/api/v1/budgets").exchange().expectStatus().isOk()
                .expectBody().jsonPath("$[0].id").isEqualTo(budgetId.toString());
        client.get().uri("/api/v1/budgets/" + budgetId).exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("Octubre");
        client.put().uri("/api/v1/budgets/" + budgetId).bodyValue(request()).exchange().expectStatus().isOk();
    }

    @Test
    void changesStatusAndReturnsConsumption() {
        when(budgets.changeStatus(userId, budgetId, false)).thenReturn(Mono.just(
                budget().changeStatus(false, LocalDateTime.now())));
        when(consumption.getConsumption(userId, budgetId)).thenReturn(Mono.just(new BudgetConsumption(
                budgetId, new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("-10"),
                BigDecimal.TEN, List.of())));
        client.patch().uri("/api/v1/budgets/" + budgetId + "/status")
                .bodyValue(new BudgetStatusRequest(false)).exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.active").isEqualTo(false);
        client.get().uri("/api/v1/budgets/" + budgetId + "/consumption").exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.exceeded").isEqualTo(10);
    }

    @Test
    void rejectsMissingStatusAndInactiveUsers() {
        client.patch().uri("/api/v1/budgets/" + budgetId + "/status")
                .bodyValue("{}").header("Content-Type", "application/json")
                .exchange().expectStatus().isBadRequest();
        when(budgets.getAll(userId)).thenReturn(Flux.error(new BudgetAccessDeniedException()));
        client.get().uri("/api/v1/budgets").exchange().expectStatus().isForbidden();
    }

    @org.springframework.boot.test.context.TestConfiguration
    @EnableWebFlux
    @Import({SecurityConfig.class, JwtService.class,
            CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class,
            BudgetController.class, BudgetExceptionHandler.class, BudgetApiMapperImpl.class})
    static class TestConfiguration {
        @Bean BudgetService budgets() { return mock(BudgetService.class); }
        @Bean BudgetConsumptionService consumption() { return mock(BudgetConsumptionService.class); }
        @Bean ReactiveUserDetailsService users() { return mock(ReactiveUserDetailsService.class); }
        @Bean JwtFilter jwtFilter(JwtService jwt, ReactiveUserDetailsService users) { return new JwtFilter(jwt, users); }
        @Bean ObjectMapper objectMapper() { return new ObjectMapper(); }
        @Bean AccountAuthorizationManager accounts() { return mock(AccountAuthorizationManager.class); }
        @Bean TransferAuthorizationManager transfers() { return mock(TransferAuthorizationManager.class); }
        @Bean CategoryAuthorizationManager categories() { return mock(CategoryAuthorizationManager.class); }
        @Bean MovementAuthorizationManager movements() { return mock(MovementAuthorizationManager.class); }
        @Bean SubscriptionAuthorizationManager subscriptions() { return mock(SubscriptionAuthorizationManager.class); }
    }
}
