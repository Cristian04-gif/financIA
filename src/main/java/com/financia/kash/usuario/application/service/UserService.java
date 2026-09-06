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

@Service
@RequiredArgsConstructor
public class UserService implements ChangePasswordUseCase, MyInformationUseCase, DeleteUserUseCase {

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoderForUserPort encoderForUserPort;

    @Override
    public void changePassword(UUID id, String newPassword) {
        User user = repositoryPort.getMe(id);
        user.changePassword(encoderForUserPort.ecoderPassword(newPassword));
        repositoryPort.save(user);
    }

    @Override
    public User findMe(UUID id) {
        return repositoryPort.getMe(id);
    }

    @Override
    public void deleteMe(UUID id) {
        User user = repositoryPort.getMe(id);
        if (user.getStatus().equals(EstadoUsuario.ELIMINADO)) {
            throw new RuntimeException("El usuario ya esta en proceso de eliminacion");
        }
        user.changeStatus(EstadoUsuario.SUSPENDIDO.name());
        repositoryPort.save(user);
    }

    @Override
    public User findMe(String email) {
        return repositoryPort.getMe(email);
    }

}
