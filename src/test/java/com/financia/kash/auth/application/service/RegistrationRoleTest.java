package com.financia.kash.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import com.financia.kash.auth.application.port.output.*;
import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.model.User;
import reactor.core.publisher.Mono;

class RegistrationRoleTest {
    @Test
    void publicRegistrationIgnoresRequestedAdminRole() {
        var authentication = mock(AuthenticationPort.class);
        var encoder = mock(PasswordEncoderPort.class);
        var emails = mock(UserEmailForAuthenticationPort.class);
        var users = mock(UserSaveForAuthPort.class);
        when(emails.existEmail(anyString())).thenReturn(Mono.just(false));
        when(encoder.ecoderPassword(anyString())).thenReturn("encoded");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(RoleUser.USER, user.getRole());
            return Mono.just(user);
        });
        when(authentication.authenticate(anyString(), anyString())).thenReturn(Mono.just("token"));
        var service = new AuthService(authentication, encoder, emails, null, null, users);
        service.registerUser(new Auth("Nombre", "Apellido", "test@example.com", "Password1*", "ADMIN")).block();
        verify(users).save(any(User.class));
    }
}
