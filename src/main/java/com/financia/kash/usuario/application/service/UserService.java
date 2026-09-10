package com.financia.kash.usuario.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.usuario.application.port.input.ChangePasswordUseCase;
import com.financia.kash.usuario.application.port.input.DeleteUserUseCase;
import com.financia.kash.usuario.application.port.input.MyInformationUseCase;
import com.financia.kash.usuario.application.port.output.PasswordEncoderForUserPort;
import com.financia.kash.usuario.application.port.output.UserRepositoryPort;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService implements ChangePasswordUseCase, MyInformationUseCase, DeleteUserUseCase {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoderForUserPort encoderForUserPort;

    @Override
    public Mono<Void> deleteMe(UUID id) {
        return repositoryPort.getMe(id).flatMap(user -> {
            if (user.getStatus().equals(EstadoUsuario.ELIMINADO)) {
                throw new RuntimeException("El usuario ya esta en proceso de eliminacion");
            }
            user.changeStatus(EstadoUsuario.SUSPENDIDO.name());
            return repositoryPort.save(user);
        }).then();
    }

    @Override
    public Mono<User> findMe(UUID id) {
        return repositoryPort.getMe(id);
    }

    @Override
    public Mono<User> findMe(String email) {
        return repositoryPort.getMe(email);
    }

    @Override
    public Mono<Void> changePassword(UUID id, String newPassword) {
        Mono<User> userMono = repositoryPort.getMe(id);
        Mono<String> encoderPasswordMono = encoderForUserPort.ecoderPassword(newPassword);

        return Mono.zip(userMono, encoderPasswordMono).flatMap(tuple -> {
            User user = tuple.getT1();
            String passwordEncoder = tuple.getT2();
            user.changePassword(passwordEncoder);
            return repositoryPort.save(user);
        }).then();
    }

}
