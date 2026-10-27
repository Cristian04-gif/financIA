package com.financia.kash.shared.infrastructure.utils.event;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.enums.UserStatus;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.admin.bootstrap.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Value("${app.admin.bootstrap.email}")
    private String email;

    @Value("${app.admin.bootstrap.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        if (email == null || email.isBlank() || password == null || password.length() < 12) {
            throw new IllegalArgumentException("Bootstrap admin requiere email y contraseña de al menos 12 caracteres");
        }
        userEntityRepository.findByEmail(email).switchIfEmpty(Mono.fromCallable(() -> {
            UserEntity entity = new UserEntity(null, "admin", "principal", email,
                    passwordEncoderPort.ecoderPassword(password), LocalDate.now(), null, UserStatus.ACTIVO,
                    RoleUser.ADMIN,
                    null, false);

            return entity;
        }).flatMap(userEntityRepository::save).doOnNext(e -> {
            System.out.println("Se guardo el admin: " + e.getEmail());
        })).block();
    }

}
