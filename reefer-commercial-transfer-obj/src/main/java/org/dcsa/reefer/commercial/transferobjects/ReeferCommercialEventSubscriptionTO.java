package org.dcsa.reefer.commercial.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReeferCommercialEventSubscriptionTO(
  @JsonProperty("subscriptionID")
  UUID id,

  @NotBlank
  String callbackUrl,

  @ISO6346EquipmentReference
  String equipmentReference,

  @Size(max = 35)
  String carrierBookingReference,

  @JsonProperty("subscriptionCreatedDateTime")
  OffsetDateTime createdDateTime,

  @JsonProperty("subscriptionUpdatedDateTime")
  OffsetDateTime updatedDateTime
) {
  @Builder(toBuilder = true)
  public ReeferCommercialEventSubscriptionTO { }
}
