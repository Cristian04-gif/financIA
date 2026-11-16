package com.financia.kash.administracion.infrastructure.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.*;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.context.annotation.*;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.core.*;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.financia.kash.administracion.application.port.input.*;
import com.financia.kash.administracion.application.service.*;
import com.financia.kash.administracion.domain.exception.*;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapperImpl;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.shared.infrastructure.config.UuidCallback;
import com.financia.kash.usuario.domain.enums.*;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.*;
import reactor.core.publisher.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "ADMIN_TEST_DATABASE_URL", matches = ".+")
class AdminPostgresTest {
    private static final String SCHEMA = "admin_test_" + UUID.randomUUID().toString().replace("-", "");
    private AnnotationConfigApplicationContext context;
    private DatabaseClient db;
    private ManageUsersUseCase users;
    private ManageGlobalCategoriesUseCase categories;
    private GetAdminAuditUseCase audit;
    private UUID actor;
    private UUID target;

    private java.sql.Connection jdbc() throws Exception {
        var options = ConnectionFactoryOptions.parse(System.getenv("ADMIN_TEST_DATABASE_URL"));
        String url = "jdbc:postgresql://" + options.getRequiredValue(ConnectionFactoryOptions.HOST) + ":"
                + options.getRequiredValue(ConnectionFactoryOptions.PORT) + "/"
                + options.getRequiredValue(ConnectionFactoryOptions.DATABASE);
        Object password = options.getValue(ConnectionFactoryOptions.PASSWORD);
        return DriverManager.getConnection(url, options.getRequiredValue(ConnectionFactoryOptions.USER).toString(),
                password == null ? "" : password.toString());
    }

    @BeforeAll
    void setup() throws Exception {
        try (var connection = jdbc(); var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA);
            statement.execute("SET search_path TO " + SCHEMA);
            statement.execute(Files.readString(Path.of("docker/database/init.sql"))
                    .replace("CREATE EXTENSION IF NOT EXISTS vector;", ""));
            String migration = Files.readString(Path.of("docker/database/migrations/002_administracion.sql"));
            statement.execute(migration);
            statement.execute(migration);
            String dashboardMigration = Files.readString(Path.of("docker/database/migrations/003_dashboard_administrativo.sql"));
            statement.execute("DROP TABLE " + SCHEMA + ".metricas_ocr_diarias");
            statement.execute(dashboardMigration);
            statement.execute(dashboardMigration);
        }
        context = new AnnotationConfigApplicationContext(DatabaseConfiguration.class);
        db = context.getBean(DatabaseClient.class);
        users = context.getBean(ManageUsersUseCase.class);
        categories = context.getBean(ManageGlobalCategoriesUseCase.class);
        audit = context.getBean(GetAdminAuditUseCase.class);
    }

    @BeforeEach
    void seed() {
        db.sql("TRUNCATE metricas_ocr_diarias, auditoria_administrativa, usuarios CASCADE").then().block();
        actor = insertUser("admin@example.com", "ADMIN");
        target = insertUser("persona@example.com", "USER");
    }

    private UUID insertUser(String email, String role) {
        UUID id = UUID.randomUUID();
        db.sql("INSERT INTO usuarios(id,nombre,apellido,email,\"contraseña\",fecha_creacion,estado,rol,enable2fa) "
                + "VALUES(:id,'Persona','Prueba',:email,'hash',CURRENT_DATE,'ACTIVO',CAST(:role AS rol_usuario),false)")
                .bind("id", id).bind("email", email).bind("role", role).then().block();
        return id;
    }

    @AfterAll
    void cleanup() throws Exception {
        if (context != null) context.close();
        try (var connection = jdbc(); var statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
        }
    }

    @Test
    void userSearchAndStatusChangesAreAuditedWithoutOverwritingCredentials() {
        var page = users.search("PERSONA@", UserStatus.ACTIVO, 0, 20).block(Duration.ofSeconds(10));
        assertEquals(1, page.totalElements());
        assertEquals(target, page.content().getFirst().id());
        users.changeStatus(actor, target, UserStatus.SUSPENDIDO, "Solicitud").block();
        assertEquals(UserStatus.SUSPENDIDO, users.get(target).block().status());
        assertEquals("hash", db.sql("SELECT \"contraseña\" AS password FROM usuarios WHERE id=:id")
                .bind("id", target).map((row, metadata) -> row.get("password", String.class)).one().block());
        var entries = audit.get(actor, AuditAction.USER_STATUS_CHANGED, 0, 20).block();
        assertEquals(1, entries.totalElements());
        assertEquals("SUSPENDIDO: Solicitud", entries.content().getFirst().detail());
        users.changeStatus(actor, target, UserStatus.ACTIVO, "Resuelto").block();
        assertEquals(UserStatus.ACTIVO, users.get(target).block().status());
        assertThrows(AdminConflictException.class,
                () -> users.changeStatus(target, actor, UserStatus.SUSPENDIDO, "Motivo").block());
    }

    @Test
    void auditFailureRollsBackUserChange() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> users.changeStatus(UUID.randomUUID(), target, UserStatus.SUSPENDIDO, "Motivo").block());
        assertEquals(UserStatus.ACTIVO, users.get(target).block().status());
        assertEquals(0, audit.get(null, null, 0, 20).block().totalElements());
    }

    @Test
    void globalCategoriesKeepTypeAndHistoryAndRejectPersonalIds() {
        var category = categories.create(actor, "Alimentos", CategoryType.GASTOS).block();
        categories.rename(actor, category.getId(), "Alimentacion").block();
        var inactive = categories.changeStatus(actor, category.getId(), false).block();
        assertEquals(CategoryType.GASTOS, inactive.getType());
        assertFalse(inactive.isActive());
        assertEquals(1, categories.list().count().block());
        assertEquals(3, audit.get(actor, null, 0, 20).block().totalElements());
        db.sql("UPDATE categorias SET usuario_id=:owner WHERE id=:id")
                .bind("owner", target).bind("id", category.getId()).then().block();
        assertThrows(com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException.class,
                () -> categories.rename(actor, category.getId(), "No permitido").block());
    }

    @Test
    void auditFailureRollsBackCategoryCreation() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> categories.create(UUID.randomUUID(), "Alimentos", CategoryType.GASTOS).block());
        assertEquals(0, categories.list().count().block());
    }

    @Test
    void cacheFailureRollsBackBothStateAndAudit() {
        var redis = context.getBean(ReactiveStringRedisTemplate.class);
        when(redis.opsForValue().delete(anyString())).thenReturn(Mono.error(new IllegalStateException("Redis no disponible")));
        try {
            assertThrows(IllegalStateException.class,
                    () -> users.changeStatus(actor, target, UserStatus.SUSPENDIDO, "Motivo").block());
            assertEquals(UserStatus.ACTIVO, users.get(target).block().status());
            assertEquals(0, audit.get(null, null, 0, 20).block().totalElements());
        } finally {
            when(redis.opsForValue().delete(anyString())).thenReturn(Mono.just(true));
        }
    }

    @Test
    void emptyDashboardReturnsZerosAndACompleteDailySeries() {
        db.sql("TRUNCATE usuarios CASCADE").then().block();
        var result = context.getBean(GetAdminDashboardUseCase.class).get(null, null).block();
        assertEquals(0, result.overview().users().total());
        assertEquals(0, result.overview().categories().total());
        assertEquals(0, result.overview().budgets().total());
        assertEquals(0, result.overview().goals().total());
        assertEquals(0, result.overview().ocr().total());
        assertNull(result.overview().ocr().firstRecordedDate());
        assertEquals(27, result.registrations().size());
        assertTrue(result.registrations().stream().allMatch(day -> day.count() == 0));
    }

    @Test
    void dashboardCountsStatusesAndPeriodBoundariesWithoutMixingAdministrators() {
        db.sql("UPDATE usuarios SET fecha_creacion = DATE '2026-10-10'").then().block();
        for (String status : List.of("SUSPENDIDO", "INACTIVO", "ELIMINADO", "PENDIENTE")) {
            UUID id = insertUser(status.toLowerCase() + "@example.com", "USER");
            db.sql("UPDATE usuarios SET estado = CAST(:status AS estado_usuario), fecha_creacion = DATE '2026-10-10' WHERE id=:id")
                    .bind("status", status).bind("id", id).then().block();
        }
        UUID previousUser = insertUser("previous@example.com", "USER");
        db.sql("UPDATE usuarios SET fecha_creacion = DATE '2026-09-30' WHERE id=:id").bind("id", previousUser).then().block();
        insertBudget(target, "2026-10-01T00:00:00", true);
        insertBudget(target, "2026-10-27T23:59:59", false);
        insertBudget(target, "2026-10-28T00:00:00", true);
        insertBudget(actor, "2026-10-10T00:00:00", true);
        insertGoal(target, "2026-10-27", "COMPLETADA");
        insertGoal(target, "2026-09-30", "PAUSADA");
        insertGoal(actor, "2026-10-10", "INICIADA");
        var category = categories.create(actor, "Alimentos", CategoryType.GASTOS).block();
        categories.create(actor, "Transporte", CategoryType.GASTOS).block();
        categories.changeStatus(actor, category.getId(), false).block();
        db.sql("INSERT INTO categorias(id,usuario_id,nombre,tipo,activo,fecha_creacion) "
                + "VALUES(:id,:owner,'Personal','GASTOS',true,DATE '2026-10-10')")
                .bind("id", UUID.randomUUID()).bind("owner", target).then().block();
        db.sql("INSERT INTO metricas_ocr_diarias(fecha,exitos,fallos) VALUES "
                + "('2026-09-30',100,100),('2026-10-01',2,1),('2026-10-27',3,4),('2026-10-28',10,10)").then().block();

        var result = context.getBean(GetAdminDashboardUseCase.class)
                .get(java.time.LocalDate.of(2026,10,1), java.time.LocalDate.of(2026,10,27)).block();
        var counts = result.overview();
        assertEquals(new AdminDashboard.Users(6,2,1,1,1,1,1,5), counts.users());
        assertEquals(new AdminDashboard.Categories(2,1,1), counts.categories());
        assertEquals(new AdminDashboard.Budgets(3,2,1,2), counts.budgets());
        assertEquals(new AdminDashboard.Goals(2,1,1), counts.goals());
        assertEquals(new AdminDashboard.Ocr(5,5,10,java.time.LocalDate.of(2026,9,30)), counts.ocr());
        assertEquals(5, result.registrations().get(9).count());
        assertEquals(5, result.registrations().stream().mapToLong(AdminDashboard.DailyRegistrations::count).sum());
    }

    @Test
    void concurrentOcrRequestsIncrementWithoutLostUpdatesAndUseLimaDate() {
        var clock = java.time.Clock.fixed(java.time.Instant.parse("2026-10-27T04:30:00Z"), java.time.ZoneId.of("America/Lima"));
        var metrics = new com.financia.kash.movimiento.comprobante.infrastructure.database.OcrMetricsAdapter(db, clock);
        Flux.range(0,20).flatMap(index -> metrics.record(index % 2 == 0), 8).then().block();
        var counts = db.sql("SELECT fecha,exitos,fallos FROM metricas_ocr_diarias")
                .map((row, metadata) -> new AdminDashboard.Ocr(row.get("exitos",Long.class), row.get("fallos",Long.class),
                        20, row.get("fecha",java.time.LocalDate.class))).one().block();
        assertEquals(new AdminDashboard.Ocr(10,10,20,java.time.LocalDate.of(2026,10,26)), counts);
    }

    private void insertBudget(UUID owner, String date, boolean active) {
        db.sql("INSERT INTO presupuestos(id,usuario_id,nombre,periodo_inicial,periodo_fin,monto_limite_total,activo,fecha_creacion) "
                + "VALUES(:id,:owner,'Presupuesto',DATE '2026-10-01',DATE '2026-10-31',100,:active,:date)")
                .bind("id",UUID.randomUUID()).bind("owner",owner).bind("active",active)
                .bind("date",java.time.LocalDateTime.parse(date)).then().block();
    }

    private void insertGoal(UUID owner, String date, String status) {
        db.sql("INSERT INTO metas_financieras(id,usuario_id,nombre,monto_objetivo,fecha_inicio,fecha_objetivo,estado) "
                + "VALUES(:id,:owner,'Meta',100,:date,DATE '2026-12-31',CAST(:status AS estado_meta))")
                .bind("id",UUID.randomUUID()).bind("owner",owner).bind("status",status)
                .bind("date",java.time.LocalDate.parse(date)).then().block();
    }

    @Configuration
    @EnableTransactionManagement
    @EnableR2dbcRepositories(basePackageClasses = CategoryEntityRepository.class)
    @Import({UserAdminService.class, GlobalCategoryAdminService.class, AdminAuditService.class,
            AdminUserAdapter.class, GlobalCategoryAdapter.class, AdminAuditAdapter.class,
            CategoryMapperImpl.class, UuidCallback.class, AdminDashboardService.class, AdminDashboardAdapter.class})
    static class DatabaseConfiguration extends AbstractR2dbcConfiguration {
        @Bean
        java.time.Clock reportingClock() {
            return java.time.Clock.fixed(java.time.Instant.parse("2026-10-27T12:00:00Z"), java.time.ZoneId.of("America/Lima"));
        }
        @Bean
        @Override
        public org.springframework.data.r2dbc.convert.R2dbcCustomConversions r2dbcCustomConversions() {
            return org.springframework.data.r2dbc.convert.R2dbcCustomConversions.of(
                    org.springframework.data.r2dbc.dialect.PostgresDialect.INSTANCE,
                    List.of(new com.financia.kash.movimiento.categoria.domain.enums.CategoryTypeWriteConverter()));
        }

        @Bean
        @Override
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get(ConnectionFactoryOptions.parse(System.getenv("ADMIN_TEST_DATABASE_URL"))
                    .mutate().option(Option.valueOf("schema"), SCHEMA)
                    .option(Option.valueOf("extensions"), List.of(EnumCodec.builder()
                            .withEnum("tipo_categoria", CategoryType.class).build())).build());
        }

        @Bean
        ReactiveTransactionManager transactionManager(ConnectionFactory factory) {
            return new R2dbcTransactionManager(factory);
        }

        @Bean
        ReactiveStringRedisTemplate redis() {
            var redis = mock(ReactiveStringRedisTemplate.class, RETURNS_DEEP_STUBS);
            when(redis.scan(any(ScanOptions.class))).thenReturn(Flux.empty());
            when(redis.delete(any(org.reactivestreams.Publisher.class))).thenReturn(Mono.just(0L));
            when(redis.opsForValue().delete(anyString())).thenReturn(Mono.just(true));
            return redis;
        }
    }
}
