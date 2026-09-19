package com.solorecord.solorecord_backend.identity.exception;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
