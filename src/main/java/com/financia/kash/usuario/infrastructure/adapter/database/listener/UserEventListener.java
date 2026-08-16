package com.financia.kash.usuario.infrastructure.adapter.database.listener;

import java.util.Arrays;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.auth.infrastructure.adapter.event.UserRegistrarionEvent;
import com.financia.kash.usuario.domain.exception.UserRoleNotfoundException;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.RoleUser;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserEventListener {

    private final UserEntityRepository userRepository;

    @EventListener
    @Transactional
    public void handleuserRegistration(UserRegistrarionEvent event) {
        log.info("evento escuchado, {}", event.auth().getEmail());
        boolean existRole = Arrays.stream(RoleUser.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(event.auth().getRole()));

        if (!existRole) {
            throw new UserRoleNotfoundException(event.auth().getRole());
        }

        log.info("rol existente, {}", existRole);
        UserEntity userEntity = new UserEntity(event.auth().getName(), event.auth().getLastName(),
                event.auth().getEmail(),
                event.auth().getPassword(), EstadoUsuario.PENDIENTE,
                RoleUser.valueOf(event.auth().getRole().toUpperCase()));

        userRepository.save(userEntity);
        log.info("usuario guardado");
    }
}
