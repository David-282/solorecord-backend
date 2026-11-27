package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.patient.data.model.Patient;
import com.solorecord.solorecord_backend.patient.data.repository.PatientRepository;
import com.solorecord.solorecord_backend.patient.dto.request.CompleteProfileRequest;
import com.solorecord.solorecord_backend.patient.exception.DuplicateNationalIdException;
import com.solorecord.solorecord_backend.patient.exception.ProfileAlreadyCompletedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public void completeProfile(UUID userId, CompleteProfileRequest request) {
        if (patientRepository.existsById(userId)) {
            throw new ProfileAlreadyCompletedException("Profile already completed");
        }

        if (patientRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateNationalIdException("This national ID is already registered");
        }

        Patient patient = new Patient(
                userId,
                request.getNationalId(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getAddress(),
                null
        );

        patientRepository.save(patient);
    }

    @Override
    public boolean hasCompletedProfile(UUID userId) {
        return patientRepository.existsById(userId);
    }
}
