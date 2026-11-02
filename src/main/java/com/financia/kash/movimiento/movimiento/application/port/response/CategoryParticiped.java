package com.financia.kash.movimiento.movimiento.application.port.response;

import java.util.UUID;

public record CategoryParticiped(
        UUID id,
        String name,
        String type) {

}
