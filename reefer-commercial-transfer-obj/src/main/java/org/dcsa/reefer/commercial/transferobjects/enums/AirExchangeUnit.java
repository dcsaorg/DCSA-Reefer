package org.dcsa.reefer.commercial.transferobjects.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AirExchangeUnit {
  MQH("Cubic metre per hour"),
  FQH("Cubic foot per hour")
  ;

  @Getter
  private final String description;
}
