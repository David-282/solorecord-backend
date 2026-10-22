package com.solorecord.solorecord_backend.facility.exception;

public class FacilityDoesNotExsitEception extends RuntimeException {
    public FacilityDoesNotExsitEception(String message) {
        super(message);
    }
}
