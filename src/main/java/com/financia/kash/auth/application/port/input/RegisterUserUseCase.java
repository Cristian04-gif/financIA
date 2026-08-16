package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.domain.model.Auth;
import com.financia.kash.auth.domain.model.AuthResponse;

public interface RegisterUserUseCase {
    AuthResponse registerUser(Auth auth);
}
