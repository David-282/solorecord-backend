package com.solorecord.solorecord_backend.patient.data.repository;

import com.solorecord.solorecord_backend.patient.data.model.AccessGrant;
import com.solorecord.solorecord_backend.patient.data.model.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface AccessGrantRepository extends JpaRepository<AccessGrant, UUID> {

    boolean existsByDoctorIdAndPatientIdAndFacilityIdAndStatusIn(
            UUID doctorId, UUID patientId, UUID facilityId, Collection<AccessStatus> statuses);

    List<AccessGrant> findByPatientIdAndStatusOrderByCreatedAtDesc(UUID patientId, AccessStatus status);

    List<AccessGrant> findByDoctorIdOrderByCreatedAtDesc(UUID doctorId);

}
