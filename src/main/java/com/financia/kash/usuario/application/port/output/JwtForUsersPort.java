package com.financia.kash.usuario.application.port.output;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtForUsersPort {
    String generateToken(UserDetails userDetails);
}
