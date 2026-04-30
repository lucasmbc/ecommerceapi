package io.github.lucasmbc.ecommerceapi.controller.exception;

import io.github.lucasmbc.ecommerceapi.service.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, "Business error", ex.getMessage());
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleCustomBadRequestException(CustomBadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst();
        var message = fieldError.map(error -> String.format("Invalid field '%s': %s", error.getField(), error.getDefaultMessage()))
                .orElse("Validation error");
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation error", message);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Email already exists", ex.getMessage());
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCpfAlreadyExistsException(CpfAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "CPF already exists", ex.getMessage());
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Customer already exists", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token expired", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
