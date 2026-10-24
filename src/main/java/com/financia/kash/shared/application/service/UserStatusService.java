package com.financia.kash.shared.application.service;

import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.output.UserBlockedForAuthPort;
import com.financia.kash.shared.application.port.output.UserStatusUseCase;

import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserStatusService implements UserStatusUseCase {

    private final UserBlockedForAuthPort blockedForAuthPort;
    private final CacheService<Boolean> cacheService;

    @Override
    public Mono<Boolean> isBloked(String emailUser) {
        String cacheKey = CacheKeys.userStatusBlocked(emailUser);
        return cacheService.retrieveMonoCache(cacheKey, Boolean.class)
                .switchIfEmpty(blockedForAuthPort.isBlocked(emailUser)
                        .flatMap(blocked -> cacheService.saveMonoCache(cacheKey, blocked, 30)));
    }
}
