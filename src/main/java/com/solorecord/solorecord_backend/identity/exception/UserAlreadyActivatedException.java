package com.solorecord.solorecord_backend.identity.exception;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
