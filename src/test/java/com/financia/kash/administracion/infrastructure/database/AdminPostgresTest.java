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
        }
        context = new AnnotationConfigApplicationContext(DatabaseConfiguration.class);
        db = context.getBean(DatabaseClient.class);
        users = context.getBean(ManageUsersUseCase.class);
        categories = context.getBean(ManageGlobalCategoriesUseCase.class);
        audit = context.getBean(GetAdminAuditUseCase.class);
    }

    @BeforeEach
    void seed() {
        db.sql("TRUNCATE auditoria_administrativa, usuarios CASCADE").then().block();
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

    @Configuration
    @EnableTransactionManagement
    @EnableR2dbcRepositories(basePackageClasses = CategoryEntityRepository.class)
    @Import({UserAdminService.class, GlobalCategoryAdminService.class, AdminAuditService.class,
            AdminUserAdapter.class, GlobalCategoryAdapter.class, AdminAuditAdapter.class,
            CategoryMapperImpl.class, UuidCallback.class})
    static class DatabaseConfiguration extends AbstractR2dbcConfiguration {
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
