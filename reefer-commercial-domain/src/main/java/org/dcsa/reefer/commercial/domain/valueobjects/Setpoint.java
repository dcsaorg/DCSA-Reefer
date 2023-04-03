package org.dcsa.reefer.commercial.domain.valueobjects;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.AirExchangeUnit;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.TemperatureUnit;
import org.dcsa.skernel.infrastructure.validation.AllOrNone;

@AllOrNone({"temperature", "temperatureUnit"})
@AllOrNone({"airExchange", "airExchangeUnit"})
public record Setpoint(
  Float temperature,

  TemperatureUnit temperatureUnit,

  @Min(0) @Max(100)
  Float o2,

  @Min(0) @Max(100)
  Float co2,

  @Min(0) @Max(100)
  Float humidity,

  @Min(0)
  Float airExchange,

  AirExchangeUnit airExchangeUnit
) {
  @Builder(toBuilder = true)
  public Setpoint { }
}
