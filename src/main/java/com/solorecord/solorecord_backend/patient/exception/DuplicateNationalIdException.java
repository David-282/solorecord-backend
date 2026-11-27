package com.solorecord.solorecord_backend.patient.exception;

public class DuplicateNationalIdException extends RuntimeException {
    public DuplicateNationalIdException(String message) {
        super(message);
    }
}
