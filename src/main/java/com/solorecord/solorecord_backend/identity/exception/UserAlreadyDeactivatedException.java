package com.solorecord.solorecord_backend.identity.exception;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}
