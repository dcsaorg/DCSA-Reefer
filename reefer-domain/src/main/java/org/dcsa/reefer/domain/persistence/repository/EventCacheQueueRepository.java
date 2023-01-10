package org.dcsa.reefer.domain.persistence.repository;

import jakarta.persistence.LockModeType;
import org.dcsa.reefer.domain.persistence.entity.EventCacheQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventCacheQueueRepository extends JpaRepository<EventCacheQueue, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<EventCacheQueue> findAndLockById(UUID id);
}
