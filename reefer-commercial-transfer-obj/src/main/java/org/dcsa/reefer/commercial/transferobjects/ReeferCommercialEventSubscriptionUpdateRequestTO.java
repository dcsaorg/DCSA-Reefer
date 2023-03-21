package org.dcsa.reefer.commercial.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

import java.util.UUID;

public record ReeferCommercialEventSubscriptionUpdateRequestTO(
  /* redundant, but it's in the spec */
  @NotNull
  @JsonProperty("subscriptionID")
  UUID id,

  @NotBlank
  String callbackUrl,

  @ISO6346EquipmentReference
  String equipmentReference,

  @Size(max = 35)
  String carrierBookingReference
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventSubscriptionUpdateRequestTO { }
}
