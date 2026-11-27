package com.solorecord.solorecord_backend.clinical.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ClinicalExceptionHandler {

    @ExceptionHandler(AccessNotGrantedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(AccessNotGrantedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidDiagnosisLinkException.class)
    public ResponseEntity<Map<String, String>> handleBadLink(InvalidDiagnosisLinkException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }
}
