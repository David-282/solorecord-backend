package com.solorecord.solorecord_backend.lab.data.repository;

import com.solorecord.solorecord_backend.lab.data.model.OrderStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, UUID> {

    List<TestOrder> findByFacilityIdAndStatusOrderByOrderedAtAsc(UUID facilityId, OrderStatus status);

    Optional<TestOrder> findByIdAndFacilityId(UUID id, UUID facilityId);
}
