package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 */
public record ReeferCommercialEventTO(
  @NotNull @Valid
  EventMetadataTO metadata,

  @Valid
  ReeferCommercialEventPayloadTO payload
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventTO { }
}
