package com.financia.kash.presupuesto.infrastructure.adapter.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.*;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.presupuesto.application.model.BudgetCommand;
import com.financia.kash.presupuesto.application.service.*;
import com.financia.kash.presupuesto.domain.exception.*;
import com.financia.kash.presupuesto.domain.model.*;
import com.financia.kash.presupuesto.infrastructure.adapter.database.mapping.BudgetPersistenceMapperImpl;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetEntityRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetCategoryEntityRepository;
import com.financia.kash.presupuesto.infrastructure.config.BudgetConfiguration;
import com.financia.kash.shared.infrastructure.config.UuidCallback;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import io.r2dbc.spi.*;
import reactor.test.StepVerifier;
import reactor.core.publisher.Sinks;
import org.springframework.transaction.reactive.TransactionalOperator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "BUDGET_TEST_DATABASE_URL", matches = ".+")
class BudgetPostgresTest {
    private static final String SCHEMA = "budget_test_" + UUID.randomUUID().toString().replace("-", "");
    private AnnotationConfigApplicationContext context;
    private DatabaseClient db;
    private BudgetService service;
    private BudgetRepositoryAdapter repository;
    private BudgetConsumptionService consumption;
    private final UUID userId = UUID.randomUUID();
    private final UUID foreignUserId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID globalCategoryId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID foreignAccountId = UUID.randomUUID();

    private java.sql.Connection jdbc() throws Exception {
        var options = ConnectionFactoryOptions.parse(System.getenv("BUDGET_TEST_DATABASE_URL"));
        Object port = options.getValue(ConnectionFactoryOptions.PORT);
        String url = "jdbc:postgresql://" + options.getRequiredValue(ConnectionFactoryOptions.HOST)
                + ":" + (port == null ? 5432 : port) + "/" + options.getRequiredValue(ConnectionFactoryOptions.DATABASE);
        Object password = options.getValue(ConnectionFactoryOptions.PASSWORD);
        return DriverManager.getConnection(url, options.getRequiredValue(ConnectionFactoryOptions.USER).toString(),
                password == null ? "" : password.toString());
    }

    @BeforeAll
    void initialize() throws Exception {
        try (var connection = jdbc(); var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA);
        }
        context = new AnnotationConfigApplicationContext(DatabaseConfiguration.class);
        db = context.getBean(DatabaseClient.class);
        service = context.getBean(BudgetService.class);
        repository = context.getBean(BudgetRepositoryAdapter.class);
        consumption = context.getBean(BudgetConsumptionService.class);
        // pgvector is unrelated to budgets; native PostgreSQL installations may not include it.
        String schema = Files.readString(Path.of("docker/database/init.sql"))
                .replace("CREATE EXTENSION IF NOT EXISTS vector;", "");
        new ResourceDatabasePopulator(new ByteArrayResource(schema.getBytes(StandardCharsets.UTF_8)))
                .populate(context.getBean(ConnectionFactory.class)).block(Duration.ofSeconds(15));
    }

    @AfterAll
    void cleanup() throws Exception {
        if (context != null) {
            context.close();
        }
        try (var connection = jdbc(); var statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
        }
    }

    @BeforeEach
    void fixtures() {
        db.sql("TRUNCATE usuarios, categorias, cuentas, movimientos, transferencias, suscripciones, presupuestos, presupuesto_categorias CASCADE")
                .then().block();
        insertUser(userId, "owner@example.com");
        insertUser(foreignUserId, "foreign@example.com");
        insertCategory(categoryId, userId);
        insertCategory(globalCategoryId, null);
        insertAccount(accountId, userId);
        insertAccount(foreignAccountId, foreignUserId);
    }

    private void insertUser(UUID id, String email) {
        db.sql("""
                INSERT INTO usuarios(id, nombre, apellido, email, "contraseña", fecha_creacion, estado, rol, enable2fa)
                VALUES(:id, 'Usuario', 'Prueba', :email, 'hash', CURRENT_DATE, 'ACTIVO', 'USER', false)
                """).bind("id", id).bind("email", email).then().block();
    }

    private void insertCategory(UUID id, UUID owner) {
        var query = db.sql("""
                INSERT INTO categorias(id, usuario_id, nombre, tipo, activo, fecha_creacion)
                VALUES(:id, :owner, 'Gastos', 'GASTOS', true, CURRENT_DATE)
                """).bind("id", id);
        (owner == null ? query.bindNull("owner", UUID.class) : query.bind("owner", owner)).then().block();
    }

    private void insertAccount(UUID id, UUID owner) {
        db.sql("""
                INSERT INTO cuentas(id, usuario_id, nombre, tipo, activo, saldo_inicial, saldo_actual, fecha_creacion)
                VALUES(:id, :owner, 'Efectivo', 'EFECTIVO', true, 1000, 1000, CURRENT_DATE)
                """).bind("id", id).bind("owner", owner).then().block();
    }

    private void insertMovement(UUID owner, UUID account, UUID category, String type, String amount, LocalDate date) {
        db.sql("""
                INSERT INTO movimientos(id, usuario_id, cuenta_id, categoria_id, tipo, monto, fecha_emision, fecha_registro)
                VALUES(:id, :owner, :account, :category, :type, :amount, :date, CURRENT_TIMESTAMP)
                """).bind("id", UUID.randomUUID()).bind("owner", owner).bind("account", account)
                .bind("category", category).bind("type", type).bind("amount", new BigDecimal(amount))
                .bind("date", date).then().block();
    }

    private BudgetCommand command(List<BudgetCategory> categories) {
        return new BudgetCommand("Octubre", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                new BigDecimal("100"), categories);
    }

    private Budget createBudget() {
        return service.create(userId, command(List.of(
                new BudgetCategory(null, categoryId, new BigDecimal("40"))))).block(Duration.ofSeconds(10));
    }

    @Test
    void savesAndUpdatesAnAggregateAndRestrictsOwnership() {
        Budget original = createBudget();
        assertNotNull(original.id());
        assertNotNull(original.categories().getFirst().id());
        Budget updated = service.update(userId, original.id(), command(List.of(
                new BudgetCategory(null, globalCategoryId, new BigDecimal("60"))))).block();
        assertEquals(globalCategoryId, updated.categories().getFirst().categoryId());
        assertEquals(original.creationDate(), updated.creationDate());
        assertNotNull(updated.updateDate());
        assertEquals(1, service.getAll(userId).collectList().block().size());
        assertEquals(0, service.getAll(foreignUserId).collectList().block().size());
        StepVerifier.create(service.getById(foreignUserId, original.id()))
                .expectError(BudgetNotFoundException.class).verify();
        StepVerifier.create(service.changeStatus(foreignUserId, original.id(), false))
                .expectError(BudgetNotFoundException.class).verify();
    }

    @Test
    void rollsBackHeaderAndPartialCategoriesOnFailedCreation() {
        var budget = Budget.create(userId, "Falla", command(List.of()).periodStart(), command(List.of()).periodEnd(),
                new BigDecimal("100"), List.of(
                        new BudgetCategory(null, categoryId, new BigDecimal("40")),
                        new BudgetCategory(null, UUID.randomUUID(), new BigDecimal("20"))), LocalDateTime.now());
        StepVerifier.create(repository.save(budget)).expectError().verify();
        assertEquals(0L, db.sql("SELECT COUNT(*) AS n FROM presupuestos")
                .map(row -> row.get("n", Long.class)).one().block());
        assertEquals(0L, db.sql("SELECT COUNT(*) AS n FROM presupuesto_categorias")
                .map(row -> row.get("n", Long.class)).one().block());
    }

    @Test
    void rollsBackHeaderAndDeletedAllocationsOnFailedUpdate() {
        Budget original = createBudget();
        var invalid = original.update("No guardar", original.periodStart(), original.periodEnd(),
                original.amountLimitTotal(), List.of(
                        new BudgetCategory(null, globalCategoryId, new BigDecimal("40")),
                        new BudgetCategory(null, UUID.randomUUID(), new BigDecimal("20"))), LocalDateTime.now());
        StepVerifier.create(repository.save(invalid)).expectError().verify();
        Budget restored = service.getById(userId, original.id()).block();
        assertEquals(original, restored);
    }

    @Test
    void calculatesOnlyOwnedExpensesWithinInclusiveDates() {
        Budget budget = createBudget();
        insertMovement(userId, accountId, categoryId, "EGRESO", "10", budget.periodStart());
        insertMovement(userId, accountId, categoryId, "EGRESO", "20", budget.periodEnd());
        insertMovement(userId, accountId, globalCategoryId, "EGRESO", "30", budget.periodStart().plusDays(1));
        insertMovement(userId, accountId, categoryId, "EGRESO", "500", budget.periodStart().minusDays(1));
        insertMovement(userId, accountId, categoryId, "EGRESO", "500", budget.periodEnd().plusDays(1));
        insertMovement(userId, accountId, categoryId, "INGRESO", "500", budget.periodStart());
        insertMovement(foreignUserId, foreignAccountId, globalCategoryId, "EGRESO", "500", budget.periodStart());
        var result = consumption.getConsumption(userId, budget.id()).block();
        assertEquals(new BigDecimal("60.00"), result.spent());
        assertEquals(new BigDecimal("40.00"), result.available());
        assertEquals(new BigDecimal("30.00"), result.categories().getFirst().spent());
    }

    @Test
    void supportsEmptyBudgetsAndStatusChangesPreserveAllocations() {
        var empty = service.create(userId, command(List.of())).block();
        assertTrue(empty.categories().isEmpty());
        assertEquals(new BigDecimal("0.00"), consumption.getConsumption(userId, empty.id()).block().spent());
        var original = createBudget();
        var inactive = service.changeStatus(userId, original.id(), false).block();
        assertFalse(inactive.active());
        assertEquals(original.categories(), inactive.categories());
        assertFalse(service.changeStatus(userId, original.id(), false).block().active());
    }

    @Test
    void checksActualUserAndCategoryStatus() {
        insertCategory(UUID.randomUUID(), foreignUserId);
        db.sql("UPDATE categorias SET activo = false WHERE id = :id").bind("id", categoryId).then().block();
        StepVerifier.create(service.create(userId, command(List.of(
                new BudgetCategory(null, categoryId, BigDecimal.TEN)))))
                .expectError(BudgetValidationException.class).verify();
        db.sql("UPDATE usuarios SET estado = 'SUSPENDIDO' WHERE id = :id").bind("id", userId).then().block();
        StepVerifier.create(service.getAll(userId)).expectError(BudgetAccessDeniedException.class).verify();
    }

    @Test
    void savingAnEarlierConfigurationDoesNotUndoAStatusChange() {
        var original = createBudget();
        service.changeStatus(userId, original.id(), false).block();
        var staleUpdate = original.update("Actualizado", original.periodStart(), original.periodEnd(),
                original.amountLimitTotal(), original.categories(), LocalDateTime.now());
        var saved = repository.save(staleUpdate).block();
        assertFalse(saved.active());
        assertEquals("Actualizado", saved.name());
    }

    @Test
    void readsHeaderAndCategoriesFromTheSameSnapshotDuringAnUpdate() throws Exception {
        Budget original = createBudget();
        var actualCategories = context.getBean(BudgetCategoryEntityRepository.class);
        var delayedCategories = mock(BudgetCategoryEntityRepository.class);
        Sinks.One<Void> headerRead = Sinks.one();
        Sinks.One<Void> continueRead = Sinks.one();
        when(delayedCategories.findAllByBudgetIdOrderByCategoryIdAsc(original.id())).thenAnswer(invocation -> {
            headerRead.tryEmitEmpty();
            return actualCategories.findAllByBudgetIdOrderByCategoryIdAsc(original.id())
                    .delaySubscription(continueRead.asMono());
        });
        var reader = new BudgetRepositoryAdapter(context.getBean(BudgetEntityRepository.class),
                delayedCategories, context.getBean(BudgetPersistenceMapperImpl.class),
                context.getBean("budgetTransactionalOperator", TransactionalOperator.class),
                context.getBean("budgetReadTransactionalOperator", TransactionalOperator.class));
        var pending = reader.findByIdAndUserId(original.id(), userId).toFuture();
        try {
            headerRead.asMono().block(Duration.ofSeconds(10));
            service.update(userId, original.id(), command(List.of(
                    new BudgetCategory(null, globalCategoryId, new BigDecimal("60"))))).block();
        } finally {
            continueRead.tryEmitEmpty();
        }
        assertEquals(original, pending.get(10, java.util.concurrent.TimeUnit.SECONDS));
        assertEquals(globalCategoryId, service.getById(userId, original.id()).block()
                .categories().getFirst().categoryId());
    }

    @Test
    void incrementalSchemaCanBeAppliedRepeatedly() throws Exception {
        String migration = Files.readString(Path.of("docker/database/migrations/001_presupuestos.sql"));
        var populator = new ResourceDatabasePopulator(new ByteArrayResource(migration.getBytes(StandardCharsets.UTF_8)));
        populator.populate(context.getBean(ConnectionFactory.class)).block();
        populator.populate(context.getBean(ConnectionFactory.class)).block();
        assertNotNull(createBudget().id());
    }

    @org.springframework.boot.test.context.TestConfiguration
    @EnableR2dbcRepositories(basePackageClasses = {
            BudgetEntityRepository.class, CategoryEntityRepository.class, UserEntityRepository.class})
    @Import({BudgetConfiguration.class, BudgetRepositoryAdapter.class, BudgetCategoryAdapter.class,
            BudgetOwnerAdapter.class, BudgetExpenseAdapter.class, BudgetPersistenceMapperImpl.class, UuidCallback.class})
    static class DatabaseConfiguration extends AbstractR2dbcConfiguration {
        @Bean
        @Override
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get(ConnectionFactoryOptions.parse(System.getenv("BUDGET_TEST_DATABASE_URL"))
                    .mutate().option(Option.valueOf("schema"), SCHEMA).build());
        }

        @Bean
        ReactiveTransactionManager transactionManager(ConnectionFactory factory) {
            return new R2dbcTransactionManager(factory);
        }

        @Override
        protected java.util.Collection<String> getMappingBasePackages() {
            return List.of("com.financia.kash");
        }
    }
}
