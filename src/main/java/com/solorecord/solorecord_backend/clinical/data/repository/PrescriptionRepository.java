package com.solorecord.solorecord_backend.clinical.data.repository;

import com.solorecord.solorecord_backend.clinical.data.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByPatientIdOrderByPrescribedAtDesc(UUID patientId);
}
