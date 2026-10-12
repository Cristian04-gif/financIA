package com.financia.kash.shared.infrastructure.config;

import java.util.List;

import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

import com.financia.kash.cuenta.cuenta.domain.enums.AccountType;
import com.financia.kash.cuenta.cuenta.domain.enums.AccountTypeWriteConverter;
import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.domain.enums.ObjectiveStateWriteConverter;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryTypeWriteConverter;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovementWriteConverter;
import com.financia.kash.movimiento.suscripcion.domain.enums.SubscriptionFrequency;
import com.financia.kash.movimiento.suscripcion.domain.enums.SubscriptionFrequencyWriteConverter;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.enums.RoleUserWriteConverter;
import com.financia.kash.usuario.domain.enums.UserStatus;
import com.financia.kash.usuario.domain.enums.UserStatusWriteConverter;

import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Option;

@Configuration(proxyBeanMethods = false)
public class EnumConfig {

    @Bean
    ConnectionFactoryOptionsBuilderCustomizer builderCustomizer() {
        return builder -> builder.option(Option.valueOf("extensions"),
                List.of(EnumCodec.builder()
                        .withEnum("rol_usuario", RoleUser.class)
                        .withEnum("estado_usuario", UserStatus.class)
                        .withEnum("tipo_categoria", CategoryType.class)
                        .withEnum("tipo_cuenta", AccountType.class)
                        .withEnum("tipo_movimiento", TypeMovement.class)
                        .withEnum("frecuencia_suscripcion", SubscriptionFrequency.class)
                        .withEnum("estado_meta", ObjectiveState.class)
                        .build()));
    }

    @Bean
    R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE,
                List.of(new RoleUserWriteConverter(),
                        new UserStatusWriteConverter(),
                        new CategoryTypeWriteConverter(),
                        new AccountTypeWriteConverter(),
                        new TypeMovementWriteConverter(),
                        new SubscriptionFrequencyWriteConverter(),
                        new ObjectiveStateWriteConverter()));
    }

}
