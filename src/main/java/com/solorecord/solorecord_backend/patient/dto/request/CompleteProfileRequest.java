package com.solorecord.solorecord_backend.patient.dto.request;

import com.solorecord.solorecord_backend.patient.data.model.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompleteProfileRequest {

    private String nationalId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;

}
