package com.financia.kash.shared.domain.exception;

public class UserInactiveException extends RuntimeException {

    public UserInactiveException() {
        super("El usuario no se encuntra activo, no puede realizar ninguna accion");
    }

}
