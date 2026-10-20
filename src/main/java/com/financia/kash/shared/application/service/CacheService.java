package com.financia.kash.shared.application.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.exception.RedisException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CacheService<T> {
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ReactiveStringRedisTemplate redisTemplate() {
        return redisTemplate;
    }

    public Mono<PaginationResponse<T>> retriveCachePagination(String cacheKey, Class<T> type) {
        return redisTemplate.opsForValue().get(cacheKey)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<PaginationResponse<T>>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new RedisException(e);
                    }
                });
    }

    public Mono<PaginationResponse<T>> saveCachePagination(String cacheKey, PaginationResponse<T> type) {
        try {
            String json = objectMapper.writeValueAsString(type);
            return redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(15)).thenReturn(type);
        } catch (Exception e) {
            return Mono.just(type);
        }
    }

    public Flux<T> getCacheList(String cacheKey, Class<T> type) {
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(json -> {
                    try {
                        List<T> list = objectMapper.readValue(json,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, type));
                        return Flux.fromIterable(list);
                    } catch (JsonProcessingException e) {
                        return Flux.error(new RedisException(e));
                    }
                });
    }

    public Flux<T> saveListCache(String cacheKey, List<T> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            return redisTemplate.opsForValue()
                    .set(cacheKey, json, Duration.ofMinutes(15))
                    .thenMany(Flux.fromIterable(list));
        } catch (JsonProcessingException e) {
            return Flux.fromIterable(list);
        }
    }

    public Mono<T> retrieveMonoCache(String cacheKey, Class<T> type) {
        return redisTemplate.opsForValue().get(cacheKey).map(json -> {
            try {
                return objectMapper.readValue(json, type);
            } catch (JsonProcessingException e) {
                throw new RedisException(e);
            }
        });
    }

    public Mono<T> saveMonoCache(String cacheKey, T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return redisTemplate.opsForValue()
                    .set(cacheKey, json, Duration.ofMinutes(15))
                    .thenReturn(object);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
