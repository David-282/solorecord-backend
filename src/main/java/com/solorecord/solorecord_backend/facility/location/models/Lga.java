package com.solorecord.solorecord_backend.facility.location.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"state_id", "name"}))
public class Lga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID stateId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    private String wikidataId;

    public Lga(UUID stateId, String name, String code,
               BigDecimal latitude, BigDecimal longitude, String wikidataId) {
        this.stateId = stateId;
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wikidataId = wikidataId;
    }
}
