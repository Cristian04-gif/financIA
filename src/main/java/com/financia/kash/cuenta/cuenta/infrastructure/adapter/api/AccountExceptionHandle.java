package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.cuenta.cuenta.domain.exception.AccountNotFoundException;
import com.financia.kash.cuenta.cuenta.domain.exception.InactiveAccountException;
import com.financia.kash.cuenta.cuenta.domain.exception.InsufficientFundsException;
import com.financia.kash.cuenta.cuenta.domain.exception.InvalidAmountException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class AccountExceptionHandle {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> accountNotFound(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> inactiveAccount(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> insiffucientFunds(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> invalidAmount(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }
}
