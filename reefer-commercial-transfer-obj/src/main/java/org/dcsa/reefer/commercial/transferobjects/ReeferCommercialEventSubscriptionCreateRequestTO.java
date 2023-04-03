package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.ToString;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

public record ReeferCommercialEventSubscriptionCreateRequestTO(
  @NotBlank
  String callbackUrl,

  @ISO6346EquipmentReference
  String equipmentReference,

  @Size(max = 35)
  String carrierBookingReference,

  @NotNull
  @Size(min = ReeferCommercialEventSubscriptionUpdateSecretRequestTO.MIN_SECRET_SIZE, max = ReeferCommercialEventSubscriptionUpdateSecretRequestTO.MAX_SECRET_SIZE)
  @ToString.Exclude
  byte[] secret
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventSubscriptionCreateRequestTO { }
}
