package com.solorecord.solorecord_backend.lab.data.repository;

import com.solorecord.solorecord_backend.lab.data.model.ResultStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, UUID> {

    Optional<TestResult> findByIdAndFacilityId(UUID id, UUID facilityId);

    boolean existsByTestOrderId(UUID testOrderId);

    boolean existsByIdAndPatientIdAndStatus(UUID id, UUID patientId, ResultStatus status);

    List<TestResult> findByPatientIdAndStatusOrderByCompletedAtDesc(UUID patientId, ResultStatus status);
}
