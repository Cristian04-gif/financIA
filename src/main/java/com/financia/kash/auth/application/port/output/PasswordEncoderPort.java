package com.financia.kash.auth.application.port.output;

public interface PasswordEncoderPort {
    String ecoderPassword(String password);
}
