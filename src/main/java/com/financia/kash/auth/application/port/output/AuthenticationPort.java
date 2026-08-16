package com.financia.kash.auth.application.port.output;

public interface AuthenticationPort {
    String authenticate(String username, String password);
}
