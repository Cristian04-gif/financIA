package com.financia.kash.meta.domain.exception;

import com.financia.kash.meta.domain.model.ObjectiveState;

public class UnauthorizedContributionException extends RuntimeException {

    public UnauthorizedContributionException(ObjectiveState objectiveState) {

        super("Su meta de ahorro se encuentra '" + objectiveState + "' y no pueder contribuir a ahorros");

    }

}
