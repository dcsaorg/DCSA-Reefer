package org.dcsa.reefer.commercial.transferobjects.unofficial;

import lombok.Builder;

public record ReeferCommercialEventStatusTO(
  String eventID
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventStatusTO { }
}
