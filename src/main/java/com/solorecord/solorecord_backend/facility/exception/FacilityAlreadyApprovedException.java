package com.solorecord.solorecord_backend.facility.exception;

public class FacilityAlreadyApprovedException extends RuntimeException {
    public FacilityAlreadyApprovedException(String message) {
        super(message);
    }
}
