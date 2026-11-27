package com.solorecord.solorecord_backend.patient.exception;

public class TooManyLookupAttemptsException extends RuntimeException {
    public TooManyLookupAttemptsException(String message) {
        super(message);
    }
}
