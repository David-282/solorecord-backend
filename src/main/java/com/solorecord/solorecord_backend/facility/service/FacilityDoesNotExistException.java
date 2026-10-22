package com.solorecord.solorecord_backend.facility.service;

public class FacilityDoesNotExistException extends RuntimeException {
    public FacilityDoesNotExistException(String message) {
        super(message);
    }
}
