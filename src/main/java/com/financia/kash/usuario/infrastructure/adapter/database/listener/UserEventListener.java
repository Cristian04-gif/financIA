package com.financia.kash.usuario.infrastructure.adapter.database.listener;

import java.util.Arrays;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.auth.infrastructure.adapter.event.Activation2FAEvent;
import com.financia.kash.auth.infrastructure.adapter.event.Request2faEvent;
import com.financia.kash.auth.infrastructure.adapter.event.UserRegistrarionEvent;
import com.financia.kash.usuario.domain.exception.UserRoleNotfoundException;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.RoleUser;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEntityRepository userRepository;

    @EventListener
    @Transactional
    public void handleuserRegistration(UserRegistrarionEvent event) {
        boolean existRole = Arrays.stream(RoleUser.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(event.auth().getRole()));

        if (!existRole) {
            throw new UserRoleNotfoundException(event.auth().getRole());
        }

        UserEntity userEntity = new UserEntity(event.auth().getName(), event.auth().getLastName(),
                event.auth().getEmail(),
                event.auth().getPassword(), EstadoUsuario.ACTIVO,
                RoleUser.valueOf(event.auth().getRole().toUpperCase()));

        userRepository.save(userEntity);
    }

    @EventListener
    @Transactional
    public void handleRequest2faEvent(Request2faEvent event) {
        UserEntity user = userRepository.findById(event.userId()).orElseThrow();
        user.setSecret2fa(event.secret());
        userRepository.save(user);
    }

    @EventListener
    @Transactional
    public void handleActivation2FAEvent(Activation2FAEvent event) {
        UserEntity user = userRepository.findById(event.userId()).orElseThrow();
        user.setEnable2fa(true);
        userRepository.save(user);
    }
}
