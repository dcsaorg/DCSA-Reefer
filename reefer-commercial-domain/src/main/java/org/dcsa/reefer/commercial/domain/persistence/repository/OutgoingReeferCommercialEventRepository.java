package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.OutgoingReeferCommercialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutgoingReeferCommercialEventRepository extends JpaRepository<OutgoingReeferCommercialEvent, UUID> {
  @Query(value = "SELECT * FROM outgoing_reefer_commercial_event WHERE next_delivery_attempt <= now() ORDER BY next_delivery_attempt FOR UPDATE SKIP LOCKED LIMIT 1", nativeQuery = true)
  Optional<OutgoingReeferCommercialEvent> findNext();

  @Query(value = "SELECT count(*) FROM outgoing_reefer_commercial_event WHERE next_delivery_attempt <= now()", nativeQuery = true)
  long countEligible();
}
