package com.financia.kash.shared.infrastructure.security;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.shared.application.port.output.UserForSharedPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserForSharedPort userForSharedPort;

    public boolean isOwner(UUID id, String emailAuth, Class<? extends Ownable> clazz) {
        Ownable entity = entityManager.find(clazz, id);

        return entity != null
                && (entity.getOwnerEmail().equals("global") ? true : Objects.equals(entity.getOwnerEmail(), emailAuth));
    }

    public boolean isSameUser(UUID userId, String emaulAuth) {
        return userForSharedPort.findById(userId).map(user -> user.getEmail().equals(emaulAuth)).orElse(false);
    }

}
