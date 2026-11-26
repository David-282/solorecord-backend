package com.solorecord.solorecord_backend.facility.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class FacilityExceptionHandler {

    @ExceptionHandler({FacilityInReviewException.class, FacilityAlreadyApprovedException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(LGADoesNotExistException.class)
    public ResponseEntity<Map<String, String>> handleBadLga(LGADoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(FacilityDoesNotExsitEception.class)
    public ResponseEntity<Map<String, String>> handleNotFound(FacilityDoesNotExsitEception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }
}