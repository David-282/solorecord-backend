package com.solorecord.solorecord_backend.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps identity exceptions, plus request-body validation errors for the whole app, to a single
 * JSON shape: {"message": "..."} (validation errors also carry an "errors" map by field).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserAlreadyExistException.class,
            UserAlreadyDeactivatedException.class,
            UserAlreadyActivatedException.class,
            InvalidAccountStateException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            InvalidOtpException.class,
            UsernameNotFoundException.class,
            IncorrectPasswordException.class
    })
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException ex) {
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({UserNotVerifiedException.class, UserNotAuthorizedException.class})
    public ResponseEntity<Map<String, String>> handleForbidden(RuntimeException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UserNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({TooManyOtpAttemptsException.class, TooManyOtpRequestsException.class})
    public ResponseEntity<Map<String, String>> handleTooManyRequests(RuntimeException ex) {
        return body(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        // Covers malformed JSON and invalid enum values (for example a role that is not allowed).
        return body(HttpStatus.BAD_REQUEST, "Malformed or invalid request body");
    }

    private static ResponseEntity<Map<String, String>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message == null ? status.getReasonPhrase() : message));
    }
}
