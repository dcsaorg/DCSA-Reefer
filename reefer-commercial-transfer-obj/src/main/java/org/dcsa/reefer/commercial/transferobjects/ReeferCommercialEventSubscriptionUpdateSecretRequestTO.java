package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ReeferCommercialEventSubscriptionUpdateSecretRequestTO {
  public static final int MIN_SECRET_SIZE = 32;
  public static final int MAX_SECRET_SIZE = 64;

  @NotNull
  @Size(min = MIN_SECRET_SIZE, max = MAX_SECRET_SIZE)
  @ToString.Exclude
  byte[] secret;
}
