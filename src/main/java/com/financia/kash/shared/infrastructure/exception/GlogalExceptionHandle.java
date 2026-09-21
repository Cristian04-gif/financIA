package com.financia.kash.shared.infrastructure.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.shared.domain.exception.ErrorResponse;
import com.financia.kash.shared.domain.exception.UserInactiveException;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlogalExceptionHandle {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> badRequest(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                errors);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(UserInactiveException.class)
    public Mono<ResponseEntity<ErrorResponse>> userInactive(UserInactiveException exception) {

        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler({ JwtException.class, InsufficientAuthenticationException.class, AccessDeniedException.class })
    public Mono<ResponseEntity<ErrorResponse>> forbidden(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> illegalArgumentException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
}
