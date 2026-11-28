package com.solorecord.solorecord_backend.lab.exception;

public class LabAccessDeniedException extends RuntimeException {
    public LabAccessDeniedException(String message) {
        super(message);
    }
}
