package com.solorecord.solorecord_backend.patient.exception;

public class InvalidAccessRequestException extends RuntimeException {
    public InvalidAccessRequestException(String message) {
        super(message);
    }
}
