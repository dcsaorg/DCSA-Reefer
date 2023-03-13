package org.dcsa.reefer.commercial.domain.valueobjects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.EventClassifierCode;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.ReeferEventTypeCode;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

import java.time.OffsetDateTime;
import java.util.List;

/**
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
final public class ReeferCommercialPayloadEvent extends ReeferCommercialEvent {
  @NotNull
  private EventClassifierCode eventClassifierCode;

  @NotNull
  private OffsetDateTime eventDateTime;

  @NotNull
  private ReeferEventTypeCode reeferEventTypeCode;

  @Valid
  private Measurements Measurements;

  @Valid
  private Setpoint setpoint;

  @Valid
  private GeoLocation geoLocation;

  @NotNull @ISO6346EquipmentReference
  private String equipmentReference;

  private List<@Valid DocumentReference> relatedDocumentReferences;
}
