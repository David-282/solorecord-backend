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
@Table(name = "allergies", indexes = @Index(name = "idx_allergy_patient", columnList = "patientId"))
public class Allergy {

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
    private String allergen;

    @Column(nullable = false, updatable = false)
    private String severity;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    public Allergy(UUID patientId, UUID doctorId, UUID facilityId, String allergen, String severity) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.allergen = allergen;
        this.severity = severity;
    }
}
