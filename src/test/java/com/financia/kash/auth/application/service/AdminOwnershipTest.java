package com.financia.kash.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.AccountAuthorizationManager;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.shared.application.port.output.UserStatusUseCase;
import reactor.core.publisher.Mono;

class AdminOwnershipTest {
    @Test
    void adminCannotAccessAnotherUsersAccount() {
        var repository = mock(AccountEntityRepository.class);
        var status = mock(UserStatusUseCase.class);
        UUID id = UUID.randomUUID();
        when(status.isBloked("admin@example.com")).thenReturn(Mono.just(true));
        when(repository.existaByIdAndOwnerEmail(id, "admin@example.com")).thenReturn(Mono.just(false));
        var auth = new UsernamePasswordAuthenticationToken("admin@example.com", "",
                java.util.List.of(new SimpleGrantedAuthority("ADMIN")));
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/accounts/my-account/" + id));
        var result = new AccountAuthorizationManager(repository, status).authorize(Mono.just(auth),
                new AuthorizationContext(exchange, Map.of("id", id.toString()))).block();
        assertFalse(result.isGranted());
    }
}
