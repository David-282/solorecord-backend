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
@Table(name = "test_orders", indexes = {
        @Index(name = "idx_test_order_facility_status", columnList = "facilityId,status"),
        @Index(name = "idx_test_order_patient", columnList = "patientId")
})
public class TestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID patientId;

    @Column(nullable = false, updatable = false)
    private UUID doctorId;

    @Column(nullable = false, updatable = false)
    private UUID facilityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TestType testType;

    /** Lower-cased, trimmed name; only set when testType is OTHER. */
    @Column(updatable = false)
    private String otherTestName;

    @Column(length = 1000, updatable = false)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    public TestOrder(UUID patientId, UUID doctorId, UUID facilityId,
                     TestType testType, String otherTestName, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.facilityId = facilityId;
        this.testType = testType;
        this.otherTestName = otherTestName;
        this.notes = notes;
        this.status = OrderStatus.PENDING;
    }

    public void fulfil() {
        this.status = OrderStatus.FULFILLED;
    }
}
