package com.authsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors (field-level)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = buildErrorBody(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Duplicate username or email
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Wrong credentials on login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid username or password",
            null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> buildErrorBody(int status, String message, Object details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", message);
        if (details != null) {
            body.put("details", details);
        }
        return body;
    }
}
