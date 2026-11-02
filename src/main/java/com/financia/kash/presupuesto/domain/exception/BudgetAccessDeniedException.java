package com.financia.kash.presupuesto.domain.exception;

public class BudgetAccessDeniedException extends RuntimeException {
    public BudgetAccessDeniedException() {
        super("Se requiere un usuario activo para administrar presupuestos");
    }
}
