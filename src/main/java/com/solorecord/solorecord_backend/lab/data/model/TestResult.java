package com.solorecord.solorecord_backend.lab.data.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "test_results", indexes = {
        @Index(name = "idx_test_result_patient", columnList = "patientId"),
        @Index(name = "idx_test_result_facility", columnList = "facilityId"),
        @Index(name = "idx_test_result_type_status", columnList = "testType,status")
})
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID patientId;

    @Column(nullable = false, updatable = false)
    private UUID labTechId;

    @Column(nullable = false, updatable = false)
    private UUID facilityId;

    /** Null for lab-initiated tests. */
    @Column(updatable = false)
    private UUID testOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TestType testType;

    @Column(updatable = false)
    private String otherTestName;

    @Column(nullable = false, length = 2000)
    private String resultValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime enteredAt;

    private LocalDateTime completedAt;

    private UUID reviewingDoctorId;

    public TestResult(UUID patientId, UUID labTechId, UUID facilityId, UUID testOrderId,
                      TestType testType, String otherTestName, String resultValue, UUID reviewingDoctorId) {
        this.patientId = patientId;
        this.labTechId = labTechId;
        this.facilityId = facilityId;
        this.testOrderId = testOrderId;
        this.testType = testType;
        this.otherTestName = otherTestName;
        this.resultValue = resultValue;
        this.reviewingDoctorId = reviewingDoctorId;
        this.status = ResultStatus.PENDING;
    }

    public boolean isCompleted() {
        return status == ResultStatus.COMPLETED;
    }

    public void updateResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public void markCompleted() {
        this.status = ResultStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void routeTo(UUID doctorId) {
        this.reviewingDoctorId = doctorId;
    }
}
