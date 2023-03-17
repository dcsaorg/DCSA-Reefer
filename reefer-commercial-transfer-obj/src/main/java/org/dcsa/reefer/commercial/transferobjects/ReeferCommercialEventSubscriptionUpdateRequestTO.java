package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.dcsa.skernel.infrastructure.validation.AtLeast;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@AtLeast(nonNullsRequired = 1, fields = {"equipmentReference", "carrierBookingReference"})
public class ReeferCommercialEventSubscriptionUpdateRequestTO {
  @NotBlank
  private String callbackUrl;

  @ISO6346EquipmentReference
  private String equipmentReference;

  @Size(max = 35)
  private String carrierBookingReference;
}
