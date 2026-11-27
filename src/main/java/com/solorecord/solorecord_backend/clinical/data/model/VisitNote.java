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
@Table(name = "visit_notes", indexes = @Index(name = "idx_visit_note_patient", columnList = "patientId"))
public class VisitNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID patientId;

    @Column(nullable = false, updatable = false)
    private UUID doctorId;

    @Column(nullable = false, updatable = false)
    private UUID facilityId;

    @Column(updatable = false)
    private String bloodPressure;

    @Column(updatable = false)
    private Double temperature;

    @Column(updatable = false)
    private Integer heartRate;

    @Column(updatable = false)
    private Integer respiratoryRate;

    @Column(updatable = false)
    private Double oxygenSaturation;

    @Column(updatable = false)
    private Double weight;

    @Column(updatable = false)
    private String otherVitals;

    @Column(nullable = false, length = 4000, updatable = false)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    public VisitNote(UUID patientId, UUID doctorId, UUID facilityId, String bloodPressure,
                     Double temperature, Integer heartRate, Integer respiratoryRate,
                     Double oxygenSaturation, Double weight, String otherVitals, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.oxygenSaturation = oxygenSaturation;
        this.weight = weight;
        this.otherVitals = otherVitals;
        this.notes = notes;
    }
}
