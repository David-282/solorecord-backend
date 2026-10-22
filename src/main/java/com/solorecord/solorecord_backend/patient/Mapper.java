package com.solorecord.solorecord_backend.patient;

import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.data.model.Role;
import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.patient.data.model.AccessGrant;
import com.solorecord.solorecord_backend.patient.dto.response.AccessGrantResponse;
import lombok.Getter;

import java.util.UUID;

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

    public static User map(String email, String phone, UUID facilityId, String fullName, String passwordHarsh) {
        User admin = new User();
        admin.setEmail(email);
        admin.setPhoneNumber(phone);
        admin.setRole(Role.FACILITY_ADMIN);
        admin.setFacilityId(facilityId);
        admin.setStatus(AccountStatus.PENDING_VERIFICATION);
        admin.setFullName(fullName);
        admin.setPasswordHash(passwordHarsh);

        return admin;
    }

}


