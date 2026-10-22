package com.solorecord.solorecord_backend.facility.location.repository;

import com.solorecord.solorecord_backend.facility.location.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository <State, UUID> {

    Optional<State> findByCode(String code);
    List<State> findAllByNameOrderByName (String name);
    List<State> findAll ();

    List<State> findAllByOrderByName();
}
