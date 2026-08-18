package com.financia.kash.auth.application.port.input;

import com.financia.kash.auth.domain.model.AuthResponse;

public interface Verify2faUseCase {
    public AuthResponse verify2fa(String preToken, String code);
}
