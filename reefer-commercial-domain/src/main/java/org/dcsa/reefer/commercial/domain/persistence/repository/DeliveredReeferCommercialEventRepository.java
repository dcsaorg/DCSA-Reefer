package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.DeliveredReeferCommercialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveredReeferCommercialEventRepository extends JpaRepository<DeliveredReeferCommercialEvent, UUID> { }
