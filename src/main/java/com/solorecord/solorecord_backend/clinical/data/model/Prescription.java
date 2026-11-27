package com.solorecord.solorecord_backend.clinical.data.model;

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
@Table(name = "prescriptions", indexes = @Index(name = "idx_prescription_patient", columnList = "patientId"))
public class Prescription {

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
    private String medication;

    @Column(nullable = false, updatable = false)
    private String dosage;

    @Column(nullable = false, updatable = false)
    private String duration;

    @Column(updatable = false)
    private UUID diagnosisId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime prescribedAt;

    public Prescription(UUID patientId, UUID doctorId, UUID facilityId,
                        String medication, String dosage, String duration, UUID diagnosisId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.medication = medication;
        this.dosage = dosage;
        this.duration = duration;
        this.diagnosisId = diagnosisId;
    }
}
