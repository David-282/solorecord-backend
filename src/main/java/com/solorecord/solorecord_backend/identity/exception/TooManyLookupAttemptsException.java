package com.solorecord.solorecord_backend.identity.exception;

public class TooManyLookupAttemptsException extends RuntimeException {
    public TooManyLookupAttemptsException(String message) {
        super(message);
    }
}
