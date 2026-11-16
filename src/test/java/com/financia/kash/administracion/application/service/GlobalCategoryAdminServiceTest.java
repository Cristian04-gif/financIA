package com.financia.kash.administracion.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.financia.kash.administracion.application.port.output.AdminAuditPort;
import com.financia.kash.administracion.application.port.output.GlobalCategoryPort;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import reactor.core.publisher.Mono;

class GlobalCategoryAdminServiceTest {
    private AdminAuditPort audit() {
        var audit = mock(AdminAuditPort.class);
        when(audit.append(any(), any(), any(), any())).thenReturn(Mono.empty());
        return audit;
    }
    @Test
    void renameWaitsForCacheInvalidationAndPreservesCategoryType() {
        var port = mock(GlobalCategoryPort.class);
        UUID id = UUID.randomUUID();
        Category category = new Category(id, null, "Alimentos", CategoryType.GASTOS,
                null, LocalDate.now(), LocalDate.now(), true);
        when(port.rename(id, "Alimentos")).thenReturn(Mono.just(category));
        boolean[] invalidated = {false};
        when(port.invalidateCache(id)).thenReturn(Mono.fromRunnable(() -> invalidated[0] = true));
        var result = new GlobalCategoryAdminService(port, audit()).rename(UUID.randomUUID(), id, " Alimentos ").block();
        assertEquals(CategoryType.GASTOS, result.getType());
        assertTrue(invalidated[0]);
    }

    @Test
    void missingOrPersonalCategoryCannotBeChanged() {
        var port = mock(GlobalCategoryPort.class);
        UUID id = UUID.randomUUID();
        when(port.changeStatus(id, false)).thenReturn(Mono.empty());
        assertThrows(CategoryNotFoundException.class,
                () -> new GlobalCategoryAdminService(port, audit()).changeStatus(UUID.randomUUID(), id, false).block());
        verify(port, never()).invalidateCache(any());
    }
}
