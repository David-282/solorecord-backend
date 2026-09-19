package com.solorecord.solorecord_backend.identity.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @JsonIgnore
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "facility_id")
    private UUID facilityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public User(String fullName, String email, String passwordHash, String phoneNumber, UUID facilityId, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.facilityId = facilityId;
        this.role = role;
    }


//    public User(String email, String passwordHash, List<SimpleGrantedAuthority> simpleGrantedAuthorities) {
//    }


    public void activate(){
        this.status = AccountStatus.ACTIVE;
    }

    public void deactivate(){
        this.status = AccountStatus.INACTIVE;
    }


}
