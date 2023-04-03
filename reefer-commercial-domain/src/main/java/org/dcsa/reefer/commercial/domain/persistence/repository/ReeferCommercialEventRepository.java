package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReeferCommercialEventRepository
  extends JpaRepository<ReeferCommercialEvent, String>, JpaSpecificationExecutor<ReeferCommercialEvent> { }
