package com.financia.kash.presupuesto.infrastructure.adapter.api;

import java.util.LinkedHashMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import com.financia.kash.presupuesto.domain.exception.*;
import com.financia.kash.shared.domain.exception.ErrorResponse;

@RestControllerAdvice(assignableTypes = BudgetController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BudgetExceptionHandler {
    @ExceptionHandler(BudgetValidationException.class)
    public ResponseEntity<ErrorResponse> validation(BudgetValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(BudgetNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(BudgetAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(BudgetAccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> invalidRequest(WebExchangeBindException ex) {
        var fields = new LinkedHashMap<String, String>();
        ex.getFieldErrors().forEach(field -> fields.put(field.getField(), field.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "La peticion contiene campos invalidos", ex.getClass().getSimpleName(), fields));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                "Las referencias del presupuesto cambiaron o sus categorias estan duplicadas",
                "BudgetPersistenceConflict"));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, RuntimeException ex) {
        return ResponseEntity.status(status).body(new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName()));
    }
}
