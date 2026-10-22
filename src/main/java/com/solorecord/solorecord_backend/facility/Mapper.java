package com.solorecord.solorecord_backend.facility;

import com.solorecord.solorecord_backend.facility.data.model.Facility;
import com.solorecord.solorecord_backend.facility.dto.request.FacilityOnboardingRequest;
import com.solorecord.solorecord_backend.facility.dto.response.FacilityOnboardingResponse;
import org.jspecify.annotations.NonNull;

public class Mapper {


    public static @NonNull FacilityOnboardingResponse map(Facility facility, String message) {
        return new FacilityOnboardingResponse(
                facility.getId(),
                facility.getFacilityName(),
                facility.getSubmittedAt(),
                message,
                facility.getStatus()
        );
    }

    public static @NonNull Facility map(FacilityOnboardingRequest request, String licenseNumber) {
        return new Facility(
                request.getFacilityName(),
                request.getAddressLine(),
                request.getLgaId(),
                licenseNumber,
                request.getAdminFullName(),
                request.getAdminEmail().toLowerCase().strip(),
                request.getAdminPhone()
        );
    }

}

