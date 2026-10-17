package com.solorecord.solorecord_backend.patient.exception;

public class ProfileAlreadyCompletedException extends RuntimeException {
    public ProfileAlreadyCompletedException(String message) {
        super(message);
    }
}
