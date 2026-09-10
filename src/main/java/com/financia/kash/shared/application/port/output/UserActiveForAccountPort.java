package com.financia.kash.shared.application.port.output;

import java.util.UUID;

public interface UserActiveForAccountPort {
    boolean isUserActive(UUID userId);
}
