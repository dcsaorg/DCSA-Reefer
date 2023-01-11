package org.dcsa.reefer.commercial.service.mapping.domain;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.service.domain.ReeferCommercialDomainEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReeferCommercialDomainEventMapper {
  ReeferCommercialDomainEvent toDomain(ReeferCommercialEvent reeferCommercialEvent);
}
