package com.financia.kash.presupuesto.application.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.financia.kash.presupuesto.application.model.*;
import com.financia.kash.presupuesto.application.port.output.*;
import com.financia.kash.presupuesto.domain.exception.*;
import com.financia.kash.presupuesto.domain.model.*;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BudgetServiceTest {
    private final BudgetRepositoryPort repository = mock(BudgetRepositoryPort.class);
    private final BudgetCategoryPort categoryPort = mock(BudgetCategoryPort.class);
    private final BudgetOwnerPort ownerPort = mock(BudgetOwnerPort.class);
    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-06T17:00:00Z"), ZoneOffset.UTC);
    private BudgetService service;

    @BeforeEach
    void setup() {
        service = new BudgetService(repository, categoryPort, ownerPort, clock);
        when(ownerPort.isActive(userId)).thenReturn(Mono.just(true));
    }

    private BudgetCommand command() {
        return new BudgetCommand("Octubre", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                new BigDecimal("100"), List.of(new BudgetCategory(null, categoryId, new BigDecimal("40"))));
    }

    @Test
    void creationIsLazyAndAcceptsGlobalExpenseCategories() {
        when(categoryPort.findById(categoryId)).thenReturn(Mono.just(new BudgetCategoryInfo(categoryId, null, true, true)));
        when(repository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0, Budget.class)));
        Mono<Budget> result = service.create(userId, command());
        verifyNoInteractions(categoryPort, repository);
        StepVerifier.create(result).assertNext(b -> {
            assertEquals(userId, b.userId());
            assertEquals(LocalDateTime.now(clock), b.creationDate());
        }).verifyComplete();
    }

    @Test
    void rejectsInactiveUsersBeforeAccessingBudgets() {
        when(ownerPort.isActive(userId)).thenReturn(Mono.just(false));
        StepVerifier.create(service.getById(userId, UUID.randomUUID()))
                .expectError(BudgetAccessDeniedException.class).verify();
        verifyNoInteractions(repository);
    }

    @Test
    void cannotReadOrUpdateAnotherUsersBudget() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndUserId(id, userId)).thenReturn(Mono.empty());
        StepVerifier.create(service.update(userId, id, command())).expectError(BudgetNotFoundException.class).verify();
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsForeignInactiveAndIncomeCategories() {
        for (BudgetCategoryInfo info : List.of(
                new BudgetCategoryInfo(categoryId, UUID.randomUUID(), true, true),
                new BudgetCategoryInfo(categoryId, userId, false, true),
                new BudgetCategoryInfo(categoryId, userId, true, false))) {
            when(categoryPort.findById(categoryId)).thenReturn(Mono.just(info));
            StepVerifier.create(service.create(userId, command())).expectError(BudgetValidationException.class).verify();
        }
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMissingCategories() {
        when(categoryPort.findById(categoryId)).thenReturn(Mono.empty());
        StepVerifier.create(service.create(userId, command())).expectError(BudgetValidationException.class).verify();
        verify(repository, never()).save(any());
    }
}

