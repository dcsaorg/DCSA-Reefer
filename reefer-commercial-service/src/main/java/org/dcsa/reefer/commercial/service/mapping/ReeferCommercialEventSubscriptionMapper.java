package org.dcsa.reefer.commercial.service.mapping;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionCreateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface ReeferCommercialEventSubscriptionMapper {
  @Mapping(target = "createdDateTime", source = "now")
  @Mapping(target = "updatedDateTime", source = "now")
  ReeferCommercialEventSubscription toDAO(ReeferCommercialEventSubscriptionCreateRequestTO request, OffsetDateTime now);

  ReeferCommercialEventSubscriptionTO toDTO(ReeferCommercialEventSubscription subscription);
}
