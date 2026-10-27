package com.financia.kash.administracion.infrastructure.api;

import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.financia.kash.administracion.application.port.input.ManageUsersUseCase;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.usuario.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Administracion - Usuarios")
public class AdminUserController {
    private final ManageUsersUseCase users;
    public record ChangeUserStatus(@NotNull UserStatus status, @NotBlank @Size(max = 500) String reason) {}

    @GetMapping
    public Mono<AdminPage<AdminUser>> list(@RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return users.search(search, status, page, size);
    }

    @GetMapping("/{id}")
    public Mono<AdminUser> get(@PathVariable UUID id) { return users.get(id); }

    @PatchMapping("/{id}/status")
    public Mono<AdminUser> status(@AuthenticationPrincipal(expression = "id") UUID actor,
            @PathVariable UUID id, @Valid @RequestBody ChangeUserStatus request) {
        return users.changeStatus(actor, id, request.status(), request.reason());
    }
}
