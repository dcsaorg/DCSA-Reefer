package org.dcsa.reefer.commercial.domain.persistence.repository;

import jakarta.persistence.LockModeType;
import org.dcsa.reefer.commercial.domain.persistence.entity.EventCacheQueue;
import org.dcsa.reefer.commercial.domain.persistence.entity.enums.EventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventCacheQueueRepository extends JpaRepository<EventCacheQueue, UUID> {
  List<EventCacheQueue> findByEventType(EventType eventType, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<EventCacheQueue> findAndLockByEventID(UUID eventID);
}
