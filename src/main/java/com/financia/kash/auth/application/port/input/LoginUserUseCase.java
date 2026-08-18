package com.financia.kash.auth.application.port.input;

import java.util.Map;

public interface LoginUserUseCase {
    Map<String, Object> loginUser(String email, String password);
}
