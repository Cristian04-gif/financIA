package com.financia.kash.movimiento.categoria.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryTypeNotfoundException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class CategoryExceptionhandle {

    @ExceptionHandler(CategoryNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> categoryNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(CategoryTypeNotfoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> categoryTypeNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }
}
