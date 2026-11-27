package com.solorecord.solorecord_backend.clinical.data.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/** Immutable once saved: a correction is a new entry. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diagnoses", indexes = @Index(name = "idx_diagnosis_patient", columnList = "patientId"))
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID patientId;

    @Column(nullable = false, updatable = false)
    private UUID doctorId;

    @Column(nullable = false, updatable = false)
    private UUID facilityId;

    @Column(nullable = false, updatable = false)
    private String condition;

    @Column(length = 2000, updatable = false)
    private String notes;

    /** Optional link to a lab result; a plain UUID until the lab module exists. */
    @Column(updatable = false)
    private UUID testResultId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime diagnosedAt;

    public Diagnosis(UUID patientId, UUID doctorId, UUID facilityId,
                     String condition, String notes, UUID testResultId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.condition = condition;
        this.notes = notes;
        this.testResultId = testResultId;
    }
}
