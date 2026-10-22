package com.solorecord.solorecord_backend.facility.data.model;

import com.solorecord.solorecord_backend.facility.exception.FacilityAlreadyApprovedException;
import com.solorecord.solorecord_backend.facility.exception.FacilityAlreadyRejectedException;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String facilityName;

    @Column(nullable = false)
    private String addressLine;

    @Column(nullable = false)
    private UUID lgaId;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String adminFullName;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String adminPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityStatus status = FacilityStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private UUID reviewedById;

    @Column(length = 500)
    private String rejectionReason;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public Facility(String facilityName,
                    String addressLine, UUID lgaId,
                    String licenseNumber, String adminFullName,
                    String adminEmail, String adminPhone) {
        this.facilityName = facilityName;
        this.addressLine = addressLine;
        this.lgaId = lgaId;
        this.licenseNumber = licenseNumber;
        this.adminFullName = adminFullName;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
    }

    public void approve (UUID reviewedById){
        if (this.status == FacilityStatus.APPROVED){
            throw new FacilityAlreadyApprovedException("This facility has already been approved");
        }

        if (this.status == FacilityStatus.REJECTED){
            throw new FacilityAlreadyRejectedException("This facility has already been rejected");
        }

        this.status = FacilityStatus.APPROVED;
        this.reviewedById = reviewedById;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject (UUID reviewedById, String rejectionReason){
        if( this.status == FacilityStatus.REJECTED){
            throw new FacilityAlreadyRejectedException("This facility has already been rejected.");
        }

        if (this.status == FacilityStatus.APPROVED){
            throw new FacilityAlreadyApprovedException("This facility has already been approved");
        }

        this.status = FacilityStatus.REJECTED;
        this.reviewedById = reviewedById;
        this.rejectionReason = rejectionReason;
        this.reviewedAt = LocalDateTime.now();

    }

}
