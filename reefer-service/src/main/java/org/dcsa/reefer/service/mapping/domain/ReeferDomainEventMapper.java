package org.dcsa.reefer.service.mapping.domain;

import org.dcsa.reefer.domain.persistence.entity.ReeferEvent;
import org.dcsa.reefer.service.domain.ReeferDomainEvent;
import org.dcsa.reefer.transferobjects.ReeferEventTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReeferDomainEventMapper {
  ReeferDomainEvent toDomain(ReeferEvent reeferEvent);
}
