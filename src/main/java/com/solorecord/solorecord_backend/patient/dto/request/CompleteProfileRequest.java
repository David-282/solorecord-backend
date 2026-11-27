package com.solorecord.solorecord_backend.patient.dto.request;

import com.solorecord.solorecord_backend.patient.data.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompleteProfileRequest {

    @NotBlank @Size(max = 50)
    private String nationalId;
    @NotNull @Past
    private LocalDate dateOfBirth;
    @NotNull
    private Gender gender;
    @NotBlank @Size(max = 255)
    private String address;

}
