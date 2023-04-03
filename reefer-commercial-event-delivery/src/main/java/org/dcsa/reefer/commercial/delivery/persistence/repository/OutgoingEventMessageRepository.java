package org.dcsa.reefer.commercial.delivery.persistence.repository;

import org.dcsa.reefer.commercial.delivery.persistence.entity.OutgoingEventMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutgoingEventMessageRepository extends JpaRepository<OutgoingEventMessage, UUID> {
  @Query(value = "SELECT * FROM outgoing_event_message WHERE next_delivery_attempt <= now() ORDER BY next_delivery_attempt FOR UPDATE SKIP LOCKED LIMIT 1", nativeQuery = true)
  Optional<OutgoingEventMessage> findNext();

  @Query(value = "SELECT count(*) FROM outgoing_event_message WHERE next_delivery_attempt <= now()", nativeQuery = true)
  long countEligible();
}
