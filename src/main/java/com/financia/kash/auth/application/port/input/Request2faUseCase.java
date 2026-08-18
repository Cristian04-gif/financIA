package com.financia.kash.auth.application.port.input;

import java.util.Map;

public interface Request2faUseCase {
    Map<String, String> setup2fa(String email);
}
