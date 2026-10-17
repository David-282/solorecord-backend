package com.solorecord.solorecord_backend.patient.data.repository;

import com.solorecord.solorecord_backend.patient.data.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository <Patient, UUID>{

    Optional<Patient> findByNationalIdAndDateOfBirth(String nationalId, LocalDate dateOfBirth);
}
