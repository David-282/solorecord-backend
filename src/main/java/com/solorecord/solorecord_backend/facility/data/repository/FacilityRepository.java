package com.solorecord.solorecord_backend.facility.data.repository;

import com.solorecord.solorecord_backend.facility.data.model.Facility;
import com.solorecord.solorecord_backend.facility.data.model.FacilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {

    Optional<Facility> findById (UUID id);

    Optional<Facility> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndStatus(String licenseNumber, FacilityStatus status);

    List<Facility> findByStatusOrderBySubmittedAtAsc(FacilityStatus status);
}
