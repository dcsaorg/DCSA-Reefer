package org.dcsa.reefer.commercial.transferobjects.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReeferEventTypeCode {
  MEAS("Measured"),
  ADJU("Adjusted")
  ;

  @Getter
  private final String description;
}
