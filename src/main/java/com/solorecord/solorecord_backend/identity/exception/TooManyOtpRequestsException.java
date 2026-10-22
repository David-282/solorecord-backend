package com.solorecord.solorecord_backend.identity.exception;

public class TooManyOtpRequestsException extends RuntimeException {
    public TooManyOtpRequestsException(String message) {
        super(message);
    }
}
