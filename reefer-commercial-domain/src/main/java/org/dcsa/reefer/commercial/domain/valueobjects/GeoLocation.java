package org.dcsa.reefer.commercial.domain.valueobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record GeoLocation(
  @Size(max = 100)
  String locationName,

  @NotBlank @Size(max = 10)
  String latitude,

  @NotBlank @Size(max = 10)
  String longitude
) {
  @Builder(toBuilder = true)
  public GeoLocation { }
}
