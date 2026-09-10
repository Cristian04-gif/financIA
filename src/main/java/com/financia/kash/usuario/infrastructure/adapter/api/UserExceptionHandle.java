package com.financia.kash.usuario.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.shared.domain.exception.ErrorResponse;
import com.financia.kash.usuario.domain.exception.UserNotFoundException;
import com.financia.kash.usuario.domain.exception.UserRoleNotfoundException;
import com.financia.kash.usuario.domain.exception.UserStateNotFoundException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class UserExceptionHandle {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> userNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(UserRoleNotfoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> userRoleNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(UserStateNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> userStateNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
}
