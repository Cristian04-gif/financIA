package com.financia.kash.auth.application.port.input;

import java.util.Map;

public interface Confirm2FARequestUseCase {
    String confirm2fa(String emailUser, Map<String, String> request);
}
