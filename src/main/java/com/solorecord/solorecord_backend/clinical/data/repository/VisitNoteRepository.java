package com.solorecord.solorecord_backend.clinical.data.repository;

import com.solorecord.solorecord_backend.clinical.data.model.VisitNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitNoteRepository extends JpaRepository<VisitNote, UUID> {

    List<VisitNote> findByPatientIdOrderByVisitedAtDesc(UUID patientId);
}
