package com.financia.kash.auth.application.service;

import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.output.UserBlockedForAuthPort;
import com.financia.kash.auth.application.port.output.UserStatusUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserStatusService implements UserStatusUseCase {

    private final UserBlockedForAuthPort blockedForAuthPort;

    public Mono<Boolean> isBloked(String emailUser) {
        return blockedForAuthPort.isBlocked(emailUser);
    }
}
