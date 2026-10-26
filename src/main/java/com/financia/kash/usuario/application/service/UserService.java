package com.financia.kash.usuario.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.movimiento.suscripcion.application.port.output.RetriveUserForSubcriptionPort;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
import com.financia.kash.usuario.application.port.input.ChangePasswordUseCase;
import com.financia.kash.usuario.application.port.input.DeleteUserUseCase;
import com.financia.kash.usuario.application.port.input.MyInformationUseCase;
import com.financia.kash.usuario.application.port.input.command.ChangePasswordCommand;
import com.financia.kash.usuario.application.port.output.PasswordEncoderForUserPort;
import com.financia.kash.usuario.application.port.output.UserRepositoryPort;
import com.financia.kash.usuario.domain.enums.UserStatus;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService
        implements ChangePasswordUseCase, MyInformationUseCase, DeleteUserUseCase, RetriveUserForSubcriptionPort {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoderForUserPort encoderForUserPort;
    private final CacheService<User> cacheService;

    @Override
    public Mono<Void> deleteMe(UUID id) {
        return repositoryPort.getMe(id).flatMap(user -> {
            if (user.getStatus().equals(UserStatus.ELIMINADO)) {
                throw new RuntimeException("El usuario ya esta en proceso de eliminacion");
            }
            user.changeStatus(UserStatus.SUSPENDIDO.name());
            return repositoryPort.save(user);
        }).flatMap(user -> CacheKeys.clearAllCacheUser(cacheService.redisTemplate(), user.getId(), user.getEmail()))
                .then();
    }

    @Override
    public Mono<User> findMe(UUID id) {
        String cacheKey = CacheKeys.user(id);
        return cacheService.retrieveMonoCache(cacheKey, User.class)
                .switchIfEmpty(repositoryPort.getMe(id))
                .flatMap(user -> cacheService.saveMonoCache(cacheKey, user, 60));
        // return repositoryPort.getMe(id);
    }

    @Override
    public Mono<User> findMe(String email) {
        String cacheKey = CacheKeys.user(email);
        return cacheService.retrieveMonoCache(cacheKey, User.class)
                .switchIfEmpty(repositoryPort.getMe(email))
                .flatMap(user -> cacheService.saveMonoCache(cacheKey, user, 60));
        // return repositoryPort.getMe(email);
    }

    @Override
    public Mono<Void> changePassword(ChangePasswordCommand command) {
        return repositoryPort.getMe(command.id()).flatMap(user -> {
            user.changePassword(encoderForUserPort.ecoderPassword(command.newPassword()));
            return repositoryPort.save(user);
        }).flatMap(user -> CacheKeys.clearAllCacheUser(cacheService.redisTemplate(), user.getId(), user.getEmail()))
                .then();
    }

}
