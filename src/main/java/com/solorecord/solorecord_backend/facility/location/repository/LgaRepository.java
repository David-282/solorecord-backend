package com.solorecord.solorecord_backend.facility.location.repository;

import com.solorecord.solorecord_backend.facility.location.models.Lga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LgaRepository extends JpaRepository <Lga, UUID> {

    Optional<Lga> findByCode(String code);

    List<Lga> findByStateIdOrderByName(UUID stateId);
}
