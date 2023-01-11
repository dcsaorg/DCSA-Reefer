package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.EventCacheQueueDead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventCacheQueueDeadRepository extends JpaRepository<EventCacheQueueDead, UUID> { }
