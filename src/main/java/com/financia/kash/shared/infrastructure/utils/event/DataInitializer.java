package com.financia.kash.shared.infrastructure.utils.event;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.enums.UserStatus;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void run(String... args) throws Exception {
        userEntityRepository.findByEmail("admin@admin.com").switchIfEmpty(Mono.fromCallable(() -> {
            UserEntity entity = new UserEntity(null, "admin", "principal", "admin@admin.com",
                    passwordEncoderPort.ecoderPassword("Adm1n1str4d0r*"), LocalDate.now(), null, UserStatus.ACTIVO,
                    RoleUser.ADMIN,
                    null, false);

            return entity;
        }).flatMap(userEntityRepository::save).doOnNext(e -> {
            System.out.println("Se guardo el admin: " + e.getEmail());
        })).subscribe();
    }

}
