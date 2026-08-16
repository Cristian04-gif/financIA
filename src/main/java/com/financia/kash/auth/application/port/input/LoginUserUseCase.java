package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.domain.model.AuthResponse;

public interface LoginUserUseCase {
    AuthResponse loginUser(String email, String password);
}
