package com.financia.kash.administracion.infrastructure.api;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.financia.kash.administracion.domain.exception.*;
import com.financia.kash.shared.domain.exception.ErrorResponse;

@RestControllerAdvice(basePackageClasses = AdminUserController.class)
public class AdminExceptionHandler {
    @ExceptionHandler(AdminUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(AdminUserNotFoundException error) {
        return new ErrorResponse(error.getMessage(), "ADMIN_USER_NOT_FOUND");
    }

    @ExceptionHandler(AdminConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(AdminConflictException error) {
        return new ErrorResponse(error.getMessage(), "ADMIN_CONFLICT");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalid(IllegalArgumentException error) {
        return new ErrorResponse(error.getMessage(), "ADMIN_VALIDATION");
    }
}
