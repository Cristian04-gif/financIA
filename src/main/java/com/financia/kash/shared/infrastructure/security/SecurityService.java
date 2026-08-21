package com.financia.kash.shared.infrastructure.security;

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

    public boolean isParticipant(String id, String emailAuth, Class<? extends Ownable> clazz) {
        Ownable entity = entityManager.find(clazz, id);

        if (entity == null) {
            return false;
        }

        return entity.getOwnerEmail().stream().anyMatch(email -> {
            return email.equals(emailAuth);
        });
    }

    public boolean isSameUser(UUID userId, String emaulAuth) {
        return userForSharedPort.findById(userId).map(user -> user.getEmail().equals(emaulAuth)).orElse(false);
    }

}
