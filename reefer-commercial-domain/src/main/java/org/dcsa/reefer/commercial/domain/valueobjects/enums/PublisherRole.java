package org.dcsa.reefer.commercial.domain.valueobjects.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PublisherRole {
  /* Carrier */
  CA("Carrier"),
  AG("Carrier Local Agent"),

  /* Service Provider */
  VSP("Visibility Service Provider"),
  SVP("Any other service provider")
  ;

  @Getter
  private final String description;
}
