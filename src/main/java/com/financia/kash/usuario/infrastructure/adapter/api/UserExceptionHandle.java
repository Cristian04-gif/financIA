package com.financia.kash.usuario.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.shared.domain.exception.ErrorResponse;
import com.financia.kash.usuario.domain.exception.UserNotFoundException;
import com.financia.kash.usuario.domain.exception.UserRoleNotfoundException;
import com.financia.kash.usuario.domain.exception.UserStateNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class UserExceptionHandle {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserRoleNotfoundException.class)
    public ResponseEntity<ErrorResponse> userRoleNotFound(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserStateNotFoundException.class)
    public ResponseEntity<ErrorResponse> userStateNotFound(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
