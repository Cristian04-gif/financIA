package com.financia.kash.administracion.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.financia.kash.administracion.application.port.output.AdminUserPort;
import com.financia.kash.administracion.domain.exception.*;
import com.financia.kash.administracion.domain.model.AdminUser;
import com.financia.kash.usuario.domain.enums.*;
import reactor.core.publisher.*;

class UserAdminServiceTest {
    private final AdminUserPort port = mock(AdminUserPort.class);
    private final UserAdminService service = new UserAdminService(port);
    private final UUID actor = UUID.randomUUID();
    private final UUID target = UUID.randomUUID();

    private AdminUser user(RoleUser role, UserStatus status) {
        return new AdminUser(target, "Nombre", "Apellido", "user@example.com", role, status, LocalDate.now(), null);
    }

    @Test
    void suspensionWaitsForCacheInvalidation() {
        var suspended = user(RoleUser.USER, UserStatus.SUSPENDIDO);
        when(port.find(target)).thenReturn(Mono.just(user(RoleUser.USER, UserStatus.ACTIVO)));
        when(port.changeStatus(target, UserStatus.SUSPENDIDO)).thenReturn(Mono.just(suspended));
        boolean[] invalidated = {false};
        when(port.invalidateCache(suspended)).thenReturn(Mono.fromRunnable(() -> invalidated[0] = true));
        assertEquals(suspended, service.changeStatus(actor, target, UserStatus.SUSPENDIDO, "Solicitud de soporte").block());
        assertTrue(invalidated[0]);
    }

    @Test
    void cannotChangeOwnStatusOrManageAdministrators() {
        assertThrows(AdminConflictException.class,
                () -> service.changeStatus(actor, actor, UserStatus.SUSPENDIDO, "Motivo").block());
        when(port.find(target)).thenReturn(Mono.just(user(RoleUser.ADMIN, UserStatus.ACTIVO)));
        assertThrows(AdminConflictException.class,
                () -> service.changeStatus(actor, target, UserStatus.SUSPENDIDO, "Motivo").block());
        verify(port, never()).changeStatus(any(), any());
    }

    @Test
    void deletedUsersCannotBeReactivated() {
        when(port.find(target)).thenReturn(Mono.just(user(RoleUser.USER, UserStatus.ELIMINADO)));
        assertThrows(AdminConflictException.class,
                () -> service.changeStatus(actor, target, UserStatus.ACTIVO, "Motivo").block());
        verify(port, never()).changeStatus(any(), any());
    }

    @Test
    void reasonAndAllowedTransitionAreRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(actor, target, UserStatus.ACTIVO, " ").block());
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(actor, target, UserStatus.ELIMINADO, "Motivo").block());
        verifyNoInteractions(port);
    }

    @Test
    void missingUsersReturnNotFoundAndConcurrentChangesReturnConflict() {
        when(port.find(target)).thenReturn(Mono.empty());
        assertThrows(AdminUserNotFoundException.class, () -> service.get(target).block());
        when(port.find(target)).thenReturn(Mono.just(user(RoleUser.USER, UserStatus.ACTIVO)));
        when(port.changeStatus(target, UserStatus.SUSPENDIDO)).thenReturn(Mono.empty());
        assertThrows(AdminConflictException.class,
                () -> service.changeStatus(actor, target, UserStatus.SUSPENDIDO, "Motivo").block());
        verify(port, never()).invalidateCache(any());
    }

    @Test
    void searchIsBoundedAndIncludesPaginationMetadata() {
        assertThrows(IllegalArgumentException.class, () -> service.search("", null, -1, 20).block());
        assertThrows(IllegalArgumentException.class, () -> service.search("", null, 0, 101).block());
        when(port.search("Nombre", UserStatus.ACTIVO, 1, 20)).thenReturn(Flux.just(user(RoleUser.USER, UserStatus.ACTIVO)));
        when(port.count("Nombre", UserStatus.ACTIVO)).thenReturn(Mono.just(21L));
        var page = service.search(" Nombre ", UserStatus.ACTIVO, 1, 20).block();
        assertEquals(2, page.totalPages());
        assertEquals(21, page.totalElements());
        assertEquals(1, page.content().size());
    }
}
