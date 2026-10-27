package com.financia.kash.administracion.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.CategoryAuthorizationManager;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.shared.application.port.output.UserStatusUseCase;
import reactor.core.publisher.Mono;

class GlobalCategoryOwnershipTest {
    @Test
    void globalCategoryMayBeReadButNotModifiedThroughPersonalRoutes() {
        var repository = mock(CategoryEntityRepository.class);
        var status = mock(UserStatusUseCase.class);
        UUID id = UUID.randomUUID();
        when(status.isBloked("user@example.com")).thenReturn(Mono.just(true));
        when(repository.existsByIdAndOwnerEmail(id, "user@example.com")).thenReturn(Mono.just(true));
        when(repository.existsOwnedByIdAndEmail(id, "user@example.com")).thenReturn(Mono.just(false));
        var auth = new UsernamePasswordAuthenticationToken("user@example.com", "", List.of());
        var manager = new CategoryAuthorizationManager(repository, status);
        var get = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/categories/category/" + id));
        var put = MockServerWebExchange.from(MockServerHttpRequest.put("/api/v1/categories/of-user/" + id));
        var delete = MockServerWebExchange.from(MockServerHttpRequest.delete("/api/v1/categories/" + id));
        assertTrue(manager.authorize(Mono.just(auth), new AuthorizationContext(get, Map.of("id", id.toString()))).block().isGranted());
        assertFalse(manager.authorize(Mono.just(auth), new AuthorizationContext(put, Map.of("id", id.toString()))).block().isGranted());
        assertFalse(manager.authorize(Mono.just(auth), new AuthorizationContext(delete, Map.of("id", id.toString()))).block().isGranted());
    }
}
