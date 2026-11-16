package com.financia.kash.shared.infrastructure.utils.event;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.enums.UserStatus;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import reactor.core.publisher.Mono;

@Component

public class DataInitializer implements CommandLineRunner {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    private String email;

    private String password;

    public DataInitializer(UserEntityRepository userEntityRepository, PasswordEncoderPort passwordEncoderPort,
            @Value("${app.admin.bootstrap.email}") String email,
            @Value("${app.admin.bootstrap.password}") String password) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoderPort = passwordEncoderPort;
        this.email = email;
        this.password = password;
    }

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
        }).flatMap(userEntityRepository::save)).subscribe();
    }

}
