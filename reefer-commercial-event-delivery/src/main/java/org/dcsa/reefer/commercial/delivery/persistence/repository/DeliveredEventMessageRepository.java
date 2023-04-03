package org.dcsa.reefer.commercial.delivery.persistence.repository;

import org.dcsa.reefer.commercial.delivery.persistence.entity.DeliveredEventMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveredEventMessageRepository extends JpaRepository<DeliveredEventMessage, UUID> { }
