package com.financia.kash.presupuesto.domain.exception;

import java.util.UUID;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(UUID id) {
        super("No se encontro el presupuesto " + id);
    }
}
