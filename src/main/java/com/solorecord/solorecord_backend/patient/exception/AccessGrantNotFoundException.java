package com.solorecord.solorecord_backend.patient.exception;

public class AccessGrantNotFoundException extends RuntimeException {
    public AccessGrantNotFoundException(String message) {
        super(message);
    }
}
