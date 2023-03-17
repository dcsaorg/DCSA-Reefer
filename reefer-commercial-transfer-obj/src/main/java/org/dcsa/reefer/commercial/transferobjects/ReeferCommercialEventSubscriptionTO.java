package org.dcsa.reefer.commercial.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReeferCommercialEventSubscriptionTO extends ReeferCommercialEventSubscriptionUpdateRequestTO {
  @JsonProperty("subscriptionID")
  private UUID id;

  @JsonProperty("subscriptionCreatedDateTime")
  private OffsetDateTime createdDateTime;

  @JsonProperty("subscriptionUpdatedDateTime")
  private OffsetDateTime updatedDateTime;
}
