package org.dcsa.reefer.commercial.service.mapping.transferobject;

import org.dcsa.reefer.commercial.service.domain.ReeferCommercialDomainEvent;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReeferCommercialEventTOMapper {
  ReeferCommercialEventTO toDTO(ReeferCommercialDomainEvent reeferCommercialDomainEvent);
}
