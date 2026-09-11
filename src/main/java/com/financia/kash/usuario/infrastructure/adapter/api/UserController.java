package com.financia.kash.usuario.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.usuario.application.port.input.ChangePasswordUseCase;
import com.financia.kash.usuario.application.port.input.DeleteUserUseCase;
import com.financia.kash.usuario.application.port.input.MyInformationUseCase;
import com.financia.kash.usuario.infrastructure.adapter.api.dto.NewPasswordRequestDTO;
import com.financia.kash.usuario.infrastructure.adapter.api.dto.UserResponseDTO;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.mapping.UserMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones de la API de Usuarios")
public class UserController {

    private final MyInformationUseCase informationUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Informacion personal", description = "Devuelve la informacion de usuario logeado")
    @GetMapping
    public Mono<ResponseEntity<UserResponseDTO>> myInfo(@AuthenticationPrincipal UserEntity user) {
        UUID id = user.getId();
        return informationUseCase.findMe(id).map(userMapper::mapToDTO).map(value -> ResponseEntity.ok(value));
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Actualizar contraseña")
    @PutMapping("/password")
    public Mono<ResponseEntity<Void>> updatePassword(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid NewPasswordRequestDTO dto) {

        return changePasswordUseCase.changePassword(user.getId(), dto.newPassword())
                .thenReturn(ResponseEntity.ok().build());

    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Suspender usuario", description = "Suspende al usuario por 30 dias antes de ser eliminado")
    @DeleteMapping
    public Mono<ResponseEntity<Void>> suspendUser(@AuthenticationPrincipal UserEntity user) {
        return deleteUserUseCase.deleteMe(user.getId()).thenReturn(ResponseEntity.noContent().build());
    }

}
