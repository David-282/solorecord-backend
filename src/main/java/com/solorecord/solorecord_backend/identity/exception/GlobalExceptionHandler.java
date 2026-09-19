package com.solorecord.solorecord_backend.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserExists(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtp(InvalidOtpException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> handleIncorrectPassword(IncorrectPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyDeactivatedException.class)
    public ResponseEntity<String> handleAlreadyDeactivated(UserAlreadyDeactivatedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ResponseEntity<String> handleAlreadyActivated(UserAlreadyActivatedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<String> handleNotVerified(UserNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getMessage());
    }
}