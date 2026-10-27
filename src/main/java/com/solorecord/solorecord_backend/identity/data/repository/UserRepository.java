package com.solorecord.solorecord_backend.identity.data.repository;

import com.solorecord.solorecord_backend.identity.data.model.Role;
import com.solorecord.solorecord_backend.identity.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
//    User findByEmail(Striing emal);
    List<User> findByFacilityId(UUID facilityId);

    boolean existsByEmail(String email);

    boolean existsByIdAndRole(UUID id, Role role);

    boolean existsByRole(Role role);
}
