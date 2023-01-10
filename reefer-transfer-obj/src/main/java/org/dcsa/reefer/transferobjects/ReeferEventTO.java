package org.dcsa.reefer.transferobjects;

import lombok.Builder;

import java.util.UUID;

public record ReeferEventTO(
  UUID eventId
) {
  @Builder(toBuilder = true)
  public ReeferEventTO { }
}
