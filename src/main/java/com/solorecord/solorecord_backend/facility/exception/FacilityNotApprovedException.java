package com.solorecord.solorecord_backend.facility.exception;

public class FacilityNotApprovedException extends RuntimeException {
    public FacilityNotApprovedException(String message) {
        super(message);
    }
}
