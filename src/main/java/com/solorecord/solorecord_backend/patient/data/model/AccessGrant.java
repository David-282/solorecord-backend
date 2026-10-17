package com.solorecord.solorecord_backend.patient.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AccessGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID facilityId;

    @Column(nullable = false)
    private UUID doctorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AccessGrant(UUID patientId, UUID doctorId, UUID facilityId) {
        this.patientId= patientId;
        this.doctorId= doctorId;
        this.facilityId= facilityId;
    }


    public void grant(){
        this.status = AccessStatus.GRANTED;
    }

    public void deny(){
        this.status = AccessStatus.DENIED;
    }

    public void revoke(){
        this.status = AccessStatus.REVOKED;
    }


    public boolean isActive(){
        return this.status == AccessStatus.GRANTED;
    }



}
