package com.financia.kash.movimiento.categoria.domain.exception;

public class CategoryTypeNotfoundException extends RuntimeException {

    public CategoryTypeNotfoundException(String type) {
        super("El tipo de categoria " + type + " no existe");
    }

}
