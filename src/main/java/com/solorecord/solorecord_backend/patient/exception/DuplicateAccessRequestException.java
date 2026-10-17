package com.solorecord.solorecord_backend.patient.exception;

public class DuplicateAccessRequestException extends RuntimeException {
    public DuplicateAccessRequestException(String message) {
        super(message);
    }
}
