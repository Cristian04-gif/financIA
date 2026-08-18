package com.financia.kash.auth.application.port.output;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationPort {
    String authenticate(String username, String password);

    String preAuthenticate(String username, String password);

    boolean validatePreAuthToken(String preToken, UserDetails userDetails);

    String generateFinalTokenWithoutPassword(UserDetails userDetails);

    String getUsername(String token);
}
