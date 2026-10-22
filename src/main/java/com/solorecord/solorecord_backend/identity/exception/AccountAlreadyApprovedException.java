package com.solorecord.solorecord_backend.identity.exception;

public class AccountAlreadyApprovedException extends RuntimeException {
    public AccountAlreadyApprovedException(String message) {
        super(message);
    }
}
