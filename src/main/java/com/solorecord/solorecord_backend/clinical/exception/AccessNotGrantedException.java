package com.solorecord.solorecord_backend.clinical.exception;

public class AccessNotGrantedException extends RuntimeException {
    public AccessNotGrantedException(String message) {
        super(message);
    }
}
