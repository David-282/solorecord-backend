package com.solorecord.solorecord_backend.clinical.data.repository;

import com.solorecord.solorecord_backend.clinical.data.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, UUID> {

    List<Diagnosis> findByPatientIdOrderByDiagnosedAtDesc(UUID patientId);

    boolean existsByIdAndPatientId(UUID id, UUID patientId);
}
