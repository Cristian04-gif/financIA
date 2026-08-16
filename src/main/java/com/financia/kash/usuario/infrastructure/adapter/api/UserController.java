package com.financia.kash.usuario.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.usuario.application.service.UserService;
import com.financia.kash.usuario.domain.model.User;
import com.financia.kash.usuario.infrastructure.adapter.api.dto.NewPasswordRequestDTO;
import com.financia.kash.usuario.infrastructure.adapter.api.dto.UserResponseDTO;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.mapping.UserMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones de la API de Usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Informacion personal", description = "Devuelve la informacion de usuario logeado")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> myInfo(@AuthenticationPrincipal UserEntity user) {
        UUID id = user.getId();
        User user2 = userService.findMe(id);
        return ResponseEntity.ok(userMapper.mapToDTO(user2));
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Actualizar contraseña")
    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid NewPasswordRequestDTO dto) {
        UUID id = user.getId();
        userService.changePassword(id, dto.newPassword());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Suspender usuario", description = "Suspende al usuario por 30 dias antes de ser eliminado")
    @DeleteMapping("/me")
    public ResponseEntity<Void> suspendUser(@AuthenticationPrincipal UserEntity user) {
        UUID id = user.getId();
        userService.deleteMe(id);
        return ResponseEntity.noContent().build();
    }

}
