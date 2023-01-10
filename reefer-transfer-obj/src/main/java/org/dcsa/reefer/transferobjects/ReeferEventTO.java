package org.dcsa.reefer.transferobjects;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * TODO real TO when we have it
 */
public record ReeferEventTO(
  UUID eventId,
  OffsetDateTime eventCreatedDateTime,
  OffsetDateTime eventDateTime
) {
  @Builder(toBuilder = true)
  public ReeferEventTO { }
}
