package com.solorecord.solorecord_backend.lab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class LabExceptionHandler {

    @ExceptionHandler({OrderNotFoundException.class, ResultNotFoundException.class, PatientNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return body(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(LabAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(LabAccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(InvalidLabStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(InvalidLabStateException ex) {
        return body(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler({InvalidTestTypeException.class, InvalidReviewerException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return body(HttpStatus.BAD_REQUEST, ex);
    }

    private static ResponseEntity<Map<String, String>> body(HttpStatus status, RuntimeException ex) {
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }
}
