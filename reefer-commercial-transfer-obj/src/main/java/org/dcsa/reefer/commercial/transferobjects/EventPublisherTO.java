package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dcsa.reefer.commercial.transferobjects.enums.CarrierCodeListProvider;

public record EventPublisherTO(
  @Size(max = 100)
  @Pattern(regexp = "^\\S+(\\s+\\S+)*$")
  String partyName,

  @NotNull @Size(max = 4)
  @Pattern(regexp = "^\\S+(\\s+\\S+)*$")
  String carrierCode,

  @NotNull
  CarrierCodeListProvider carrierCodeListProvider
) {
  @Builder(toBuilder = true)
  public EventPublisherTO { }
}
