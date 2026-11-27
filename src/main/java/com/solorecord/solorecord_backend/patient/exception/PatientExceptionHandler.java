package com.solorecord.solorecord_backend.patient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PatientExceptionHandler {

    @ExceptionHandler({AccessGrantNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return body(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({InvalidAccessStateException.class, DuplicateAccessRequestException.class,
            ProfileAlreadyCompletedException.class, DuplicateNationalIdException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return body(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(InvalidAccessRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(InvalidAccessRequestException ex) {
        return body(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(TooManyLookupAttemptsException.class)
    public ResponseEntity<Map<String, String>> handleTooMany(TooManyLookupAttemptsException ex) {
        return body(HttpStatus.TOO_MANY_REQUESTS, ex);
    }

    private static ResponseEntity<Map<String, String>> body(HttpStatus status, RuntimeException ex) {
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }
}
