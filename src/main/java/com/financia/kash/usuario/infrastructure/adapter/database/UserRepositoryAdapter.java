package com.financia.kash.usuario.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.application.port.output.UserSaveForAuthPort;
import com.financia.kash.movimiento.categoria.application.port.output.UserForCategoryPort;
import com.financia.kash.movimiento.movimiento.application.port.output.UserForMovementPort;
import com.financia.kash.shared.application.port.output.UserActiveForAccountPort;
import com.financia.kash.shared.application.port.output.UserForSharedPort;
import com.financia.kash.usuario.application.port.output.UserRepositoryPort;
import com.financia.kash.usuario.domain.exception.UserNotFoundException;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.User;
import com.financia.kash.usuario.infrastructure.adapter.database.mapping.UserMapper;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter
        implements UserRepositoryPort, UserEmailForAuthenticationPort, UserForSharedPort, UserForCategoryPort,
        UserActiveForAccountPort, UserForMovementPort, UserSaveForAuthPort {

    private final UserEntityRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<Boolean> isUserActive(UUID userId) {
        return userRepository.existsByIdAndStatus(userId, EstadoUsuario.ACTIVO);
    }

    @Override
    public Mono<User> findUserById(UUID userI) {
        return userRepository.findById(userI).switchIfEmpty(Mono.error(new UserNotFoundException(userI)))
                .map(userMapper::mapToDomain);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .map(userMapper::mapToDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new UserNotFoundException(email)))
                .map(userMapper::mapToDomain);
    }

    @Override
    public Mono<User> getMe(UUID id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .map(userMapper::mapToDomain);
    }

    @Override
    public Mono<User> getMe(String email) {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new UserNotFoundException(email)))
                .map(userMapper::mapToDomain);
    }

    @Override
    public Mono<Boolean> existEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.just(userMapper.mapToEntity(user)).flatMap(userRepository::save).map(userMapper::mapToDomain);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return userRepository.deleteById(id);
    }

}
