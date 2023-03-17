package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReeferCommercialEventSubscriptionCreateRequestTO extends ReeferCommercialEventSubscriptionUpdateRequestTO {
  @NotNull
  @Size(min = ReeferCommercialEventSubscriptionUpdateSecretRequestTO.MIN_SECRET_SIZE, max = ReeferCommercialEventSubscriptionUpdateSecretRequestTO.MAX_SECRET_SIZE)
  @ToString.Exclude
  private byte[] secret;
}
