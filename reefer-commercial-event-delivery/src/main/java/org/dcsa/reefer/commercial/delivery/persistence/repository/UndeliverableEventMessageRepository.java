package org.dcsa.reefer.commercial.delivery.persistence.repository;

import org.dcsa.reefer.commercial.delivery.persistence.entity.UndeliverableEventMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UndeliverableEventMessageRepository extends JpaRepository<UndeliverableEventMessage, UUID> { }
