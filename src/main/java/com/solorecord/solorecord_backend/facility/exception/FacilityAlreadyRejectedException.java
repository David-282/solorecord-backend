package com.solorecord.solorecord_backend.facility.exception;

public class FacilityAlreadyRejectedException extends RuntimeException {
    public FacilityAlreadyRejectedException(String message) {
        super(message);
    }
}
