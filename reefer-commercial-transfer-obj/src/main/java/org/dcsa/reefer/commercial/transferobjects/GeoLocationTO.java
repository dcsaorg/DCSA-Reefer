package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record GeoLocationTO(
  @Size(max = 100)
  String locationName,

  @NotBlank @Size(max = 10)
  String latitude,

  @NotBlank @Size(max = 10)
  String longitude
) {
  @Builder(toBuilder = true)
  public GeoLocationTO { }
}
