package com.financia.kash.auth.infrastructure.adapter.event;

import java.util.UUID;

public record Request2faEvent(UUID userId, String secret) {

}
