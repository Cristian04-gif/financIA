package com.financia.kash.auth.application.port.output;

import com.financia.kash.usuario.domain.model.User;

public interface UserEmailForAuthenticationPort {
    boolean existEmail(String email);

    User findByEmail(String email);
}
