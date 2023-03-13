package org.dcsa.reefer.commercial.domain.valueobjects.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TemperatureUnit {
  CEL("Celcius"),
  FAH("Fahrenheit")
  ;

  @Getter
  private final String description;
}
