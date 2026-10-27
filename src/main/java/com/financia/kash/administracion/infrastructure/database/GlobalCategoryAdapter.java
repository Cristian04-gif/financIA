package com.financia.kash.administracion.infrastructure.database;

import java.util.UUID;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.financia.kash.administracion.application.port.output.GlobalCategoryPort;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class GlobalCategoryAdapter implements GlobalCategoryPort {
    private final CategoryEntityRepository repository;
    private final CategoryMapper mapper;
    private final ReactiveStringRedisTemplate redis;

    public Flux<Category> findAll() {
        return repository.findByUserIdIsNullAndParentCategoryIdIsNull().map(mapper::mapToDomain);
    }
    public Mono<Category> create(Category category) {
        return repository.save(mapper.mapToEntity(category)).map(mapper::mapToDomain);
    }
    public Mono<Category> rename(UUID id, String name) {
        return repository.renameGlobal(id, name).map(mapper::mapToDomain);
    }
    public Mono<Category> changeStatus(UUID id, boolean active) {
        return repository.changeGlobalStatus(id, active).map(mapper::mapToDomain);
    }
    public Mono<Void> invalidateCache(UUID id) {
        return CacheKeys.clearAllGlobalCategories(redis, id).then();
    }
}
