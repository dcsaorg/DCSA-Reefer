package org.dcsa.reefer.commercial.service.unofficial;

import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialPayloadEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialRetractedEvent;
import org.dcsa.reefer.commercial.transferobjects.EventMetadataTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventPayloadTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UnofficialReeferCommercialEventMapper {
  public ReeferCommercialEvent toDomain(ReeferCommercialEventTO eventTO) {
    return eventTO.metadata().retractedEventID() != null
      ? toRetractedEvent(eventTO.metadata()) : toPayloadEvent(eventTO.metadata(), eventTO.payload());
  }

  protected abstract ReeferCommercialPayloadEvent toPayloadEvent(EventMetadataTO metadata, ReeferCommercialEventPayloadTO payload);
  protected abstract ReeferCommercialRetractedEvent toRetractedEvent(EventMetadataTO metadata);
}
