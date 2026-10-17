package com.solorecord.solorecord_backend.patient;

import com.solorecord.solorecord_backend.patient.data.model.AccessGrant;
import com.solorecord.solorecord_backend.patient.dto.response.AccessGrantResponse;
import lombok.Getter;

@Getter
public class Mapper {

    public  static AccessGrantResponse map(AccessGrant accessGrant){

        AccessGrantResponse response = new AccessGrantResponse();
        response.setCreatedAt(accessGrant.getCreatedAt());
        response.setDoctorId(accessGrant.getDoctorId());
        response.setFacilityId(accessGrant.getFacilityId());
        response.setGrantId(accessGrant.getId());
        response.setStatus(accessGrant.getStatus());
        response.setPatientId(accessGrant.getPatientId());


        return response;
    }
}


