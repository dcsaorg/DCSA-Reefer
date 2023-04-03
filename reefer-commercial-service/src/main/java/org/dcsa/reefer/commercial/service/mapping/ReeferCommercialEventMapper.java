package org.dcsa.reefer.commercial.service.mapping;

import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialPayloadEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialRetractedEvent;
import org.dcsa.reefer.commercial.transferobjects.EventMetadataTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventPayloadTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ReeferCommercialEventMapper {
  public ReeferCommercialEventTO toDTO(ReeferCommercialEvent reeferCommercialEvent) {
    if (reeferCommercialEvent == null) {
      return null;
    } else if (reeferCommercialEvent instanceof ReeferCommercialPayloadEvent reeferCommercialPayloadEvent) {
      return ReeferCommercialEventTO.builder()
        .metadata(extractMetadataTO(reeferCommercialPayloadEvent))
        .payload(extractPayloadTO(reeferCommercialPayloadEvent))
        .build();
    } else if (reeferCommercialEvent instanceof ReeferCommercialRetractedEvent reeferCommercialRetractedEvent) {
      return ReeferCommercialEventTO.builder()
        .metadata(extractMetadataTO(reeferCommercialRetractedEvent))
        .build();
    } else {
      throw new IllegalStateException("Unknown type for reeferCommercialEvent: " + reeferCommercialEvent.getClass().getName());
    }
  }

  protected abstract EventMetadataTO extractMetadataTO(ReeferCommercialRetractedEvent reeferCommercialEvent);
  protected abstract EventMetadataTO extractMetadataTO(ReeferCommercialPayloadEvent reeferCommercialEvent);
  protected abstract ReeferCommercialEventPayloadTO extractPayloadTO(ReeferCommercialPayloadEvent reeferCommercialEvent);
}
