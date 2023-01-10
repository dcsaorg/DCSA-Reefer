package org.dcsa.reefer.service.mapping.transferobject;

import org.dcsa.reefer.service.domain.ReeferDomainEvent;
import org.dcsa.reefer.transferobjects.ReeferEventTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReeferEventTOMapper {
  ReeferEventTO toDTO(ReeferDomainEvent reeferDomainEvent);
}
