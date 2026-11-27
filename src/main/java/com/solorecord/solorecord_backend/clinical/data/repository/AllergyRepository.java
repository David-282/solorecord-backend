package com.solorecord.solorecord_backend.clinical.data.repository;

import com.solorecord.solorecord_backend.clinical.data.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, UUID> {

    List<Allergy> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
