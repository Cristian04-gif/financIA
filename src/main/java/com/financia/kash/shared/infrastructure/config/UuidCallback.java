package com.financia.kash.shared.infrastructure.config;

import java.util.UUID;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

import com.financia.kash.shared.infrastructure.utils.HasUuid;

import reactor.core.publisher.Mono;

@Component
public class UuidCallback implements BeforeConvertCallback<HasUuid> {

    @Override
    public Publisher<HasUuid> onBeforeConvert(HasUuid entity, SqlIdentifier table) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return Mono.just(entity);
    }

}
