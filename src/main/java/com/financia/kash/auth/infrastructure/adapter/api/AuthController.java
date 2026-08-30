package com.financia.kash.auth.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.auth.application.port.input.Confirm2FARequestUseCase;
import com.financia.kash.auth.application.port.input.LoginUserUseCase;
import com.financia.kash.auth.application.port.input.RegisterUserUseCase;
import com.financia.kash.auth.application.port.input.Request2faUseCase;
import com.financia.kash.auth.application.port.input.Verify2faUseCase;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;
import com.financia.kash.auth.infrastructure.adapter.api.dto.LoginRequestDTO;
import com.financia.kash.auth.infrastructure.adapter.api.dto.Verify2faRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Operaciones de la API de autentificacion")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final Request2faUseCase request2faUseCase;
    private final Confirm2FARequestUseCase confirm2faRequestUseCase;
    private final Verify2faUseCase verify2faUseCase;

    @Operation(summary = "Registro de usuario", description = "Devuelve el token de autenticacion")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid Auth auth) {
        AuthResponse authResponse = registerUserUseCase.registerUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(summary = "Inicio de sesion de usuario", description = "Devuelve el token de autenticacion")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dto) {
        Map<String, Object> authResponse = loginUserUseCase.loginUser(dto.email(), dto.password());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Solicitud de autenticacion 2fa")
    @PostMapping("/2fa/setup")
    public ResponseEntity<?> setup2fa(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Map<String, String> responseSetup = request2faUseCase.setup2fa(email);
        return ResponseEntity.ok(responseSetup);
    }

    @Operation(summary = "Confirmar autenticacion 2fa")
    @PostMapping("/2fa/confirm")
    public ResponseEntity<String> comfim2fa(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String msg = confirm2faRequestUseCase.confirm2fa(userDetails.getUsername(), request);
        return ResponseEntity.ok(msg);
    }

    @Operation(summary = "Verificacion en dos pasos")
    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verify2fa(@RequestBody @Valid Verify2faRequest request) {
        AuthResponse authResponse = verify2faUseCase.verify2fa(request.getPreToken(), request.getCode());
        return ResponseEntity.ok(authResponse);
    }

}
