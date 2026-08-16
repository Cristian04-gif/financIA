package com.financia.kash.auth.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.auth.application.service.AuthService;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;
import com.financia.kash.auth.infrastructure.adapter.api.dto.LoginRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Operaciones de la API de autentificacion")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registro de usuario", description = "Devuelve el token de autenticacion")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid Auth auth) {
        AuthResponse authResponse = authService.registerUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(summary = "Inicio de sesion de usuario", description = "Devuelve el token de autenticacion")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO dto) {
        AuthResponse authResponse = authService.loginUser(dto.email(), dto.password());
        return ResponseEntity.ok(authResponse);
    }

}
