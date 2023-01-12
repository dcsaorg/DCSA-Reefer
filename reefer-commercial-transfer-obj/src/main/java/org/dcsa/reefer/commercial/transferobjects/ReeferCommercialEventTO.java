package org.dcsa.reefer.commercial.transferobjects;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * TODO real TO when we have it
 */
public record ReeferCommercialEventTO(
  UUID eventId,
  OffsetDateTime eventCreatedDateTime,
  OffsetDateTime eventDateTime
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventTO { }
}
