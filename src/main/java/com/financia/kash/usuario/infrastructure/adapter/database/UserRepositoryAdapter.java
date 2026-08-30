package com.financia.kash.usuario.infrastructure.adapter.database;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.movimiento.categoria.application.port.output.UserForCategoryPort;
import com.financia.kash.shared.application.port.output.UserForSharedPort;
import com.financia.kash.usuario.application.port.output.UserRepositoryPort;
import com.financia.kash.usuario.domain.exception.UserNotFoundException;
import com.financia.kash.usuario.domain.model.User;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.mapping.UserMapper;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter
        implements UserRepositoryPort, UserEmailForAuthenticationPort, UserForSharedPort, UserForCategoryPort {

    private final UserEntityRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User getMe(UUID id) {
        return userRepository.findById(id).map(userMapper::mapToDomain)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.mapToEntity(user);
        UserEntity saved = userRepository.save(entity);
        return userMapper.mapToDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::mapToDomain)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public User getMe(String email) {
        return userRepository.findByEmail(email).map(userMapper::mapToDomain)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(userMapper::mapToDomain);
    }

    @Override
    public User findUserById(UUID userI) {
        return userRepository.findById(userI).map(userMapper::mapToDomain)
                .orElseThrow(() -> new UserNotFoundException(userI));
    }

}
