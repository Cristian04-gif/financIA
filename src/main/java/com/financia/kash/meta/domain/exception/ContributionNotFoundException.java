package com.financia.kash.meta.domain.exception;

import java.util.UUID;

public class ContributionNotFoundException extends RuntimeException {

    public ContributionNotFoundException(UUID id) {
        super("No se encontro la contribucion con el id: " + id);
    }

}
