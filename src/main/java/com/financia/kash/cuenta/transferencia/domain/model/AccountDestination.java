package com.financia.kash.cuenta.transferencia.domain.model;

import java.util.UUID;

public record AccountDestination(UUID accountId,
        String name) {

}
