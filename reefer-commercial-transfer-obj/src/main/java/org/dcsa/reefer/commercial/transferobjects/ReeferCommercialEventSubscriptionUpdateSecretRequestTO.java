package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.ToString;

public record ReeferCommercialEventSubscriptionUpdateSecretRequestTO(
  @NotNull
  @Size(min = MIN_SECRET_SIZE, max = MAX_SECRET_SIZE)
  @ToString.Exclude
  byte[] secret
) {
  public static final int MIN_SECRET_SIZE = 32;
  public static final int MAX_SECRET_SIZE = 64;

  @Builder(toBuilder = true)
  public ReeferCommercialEventSubscriptionUpdateSecretRequestTO { }
}
