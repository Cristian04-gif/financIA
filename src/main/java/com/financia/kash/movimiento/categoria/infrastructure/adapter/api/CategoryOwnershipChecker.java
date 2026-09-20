package com.financia.kash.movimiento.categoria.infrastructure.adapter.api;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.shared.infrastructure.Ownership.OwnershipChecker;
import com.financia.kash.shared.infrastructure.Ownership.ResourceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class CategoryOwnershipChecker implements OwnershipChecker {

    private final CategoryEntityRepository entityRepository;

    @Override
    public ResourceType supports() {
        return ResourceType.CATEGORY;
    }

    @Override
    public Mono<Boolean> isOwner(UUID resourceId, String userEmail) {
        log.info("resoruce: {}", resourceId);
        return entityRepository.existsByIdAndOwnerEmail(resourceId, userEmail);
    }

}
