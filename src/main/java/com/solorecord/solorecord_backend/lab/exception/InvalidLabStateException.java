package com.solorecord.solorecord_backend.lab.exception;

public class InvalidLabStateException extends RuntimeException {
    public InvalidLabStateException(String message) {
        super(message);
    }
}
