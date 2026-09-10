package com.financia.kash.usuario.infrastructure.adapter.database.listener;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.financia.kash.auth.infrastructure.adapter.event.Activation2FAEvent;
import com.financia.kash.auth.infrastructure.adapter.event.Request2faEvent;
import com.financia.kash.auth.infrastructure.adapter.event.UserRegistrarionEvent;
import com.financia.kash.shared.infrastructure.utils.event.DomainEventPublisher;
import com.financia.kash.usuario.domain.exception.UserRoleNotfoundException;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.RoleUser;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEntityRepository userRepository;
    private final DomainEventPublisher domainEventPublisher;

    @PostConstruct
    private void initListener() {
        domainEventPublisher.getStream()
                .ofType(UserRegistrarionEvent.class)
                .flatMap(event -> {
                    boolean existRole = Arrays.stream(RoleUser.values())
                            .anyMatch(e -> e.name().equalsIgnoreCase(event.auth().getRole()));

                    if (!existRole) {
                        throw new UserRoleNotfoundException(event.auth().getRole());
                    }
                    UserEntity userEntity = new UserEntity(event.auth().getName(), event.auth().getLastName(),
                            event.auth().getEmail(),
                            event.auth().getPassword(), EstadoUsuario.ACTIVO,
                            RoleUser.valueOf(event.auth().getRole().toUpperCase()));
                    return userRepository.save(userEntity);
                }).subscribe();

        domainEventPublisher.getStream()
                .ofType(Request2faEvent.class)
                .flatMap(event -> userRepository.findById(event.userId())
                        .flatMap(user -> {
                            user.setSecret2fa(event.secret());
                            return userRepository.save(user);
                        }))
                .subscribe();

        domainEventPublisher.getStream()
                .ofType(Activation2FAEvent.class)
                .flatMap(event -> userRepository.findById(event.userId())
                        .flatMap(user -> {
                            user.setEnable2fa(true);
                            return userRepository.save(user);
                        }))
                .subscribe();
    }

}
