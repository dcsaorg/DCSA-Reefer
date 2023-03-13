package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.dcsa.reefer.commercial.transferobjects.enums.EventClassifierCode;
import org.dcsa.reefer.commercial.transferobjects.enums.ReeferEventTypeCode;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

import java.time.OffsetDateTime;
import java.util.List;

public record ReeferCommercialEventPayloadTO(
  @NotNull
  EventClassifierCode eventClassifierCode,

  @NotNull
  OffsetDateTime eventDateTime,

  @NotNull
  ReeferEventTypeCode reeferEventTypeCode,

  @Valid
  MeasurementsTO Measurements,

  @Valid
  SetpointTO setpoint,

  @Valid
  GeoLocationTO geoLocation,

  @NotNull @ISO6346EquipmentReference
  String equipmentReference,

  List<@Valid DocumentReferenceTO> relatedDocumentReferences
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventPayloadTO { }
}
