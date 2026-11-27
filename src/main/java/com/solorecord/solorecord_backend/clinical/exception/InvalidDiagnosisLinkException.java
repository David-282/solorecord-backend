package com.solorecord.solorecord_backend.clinical.exception;

public class InvalidDiagnosisLinkException extends RuntimeException {
    public InvalidDiagnosisLinkException(String message) {
        super(message);
    }
}
