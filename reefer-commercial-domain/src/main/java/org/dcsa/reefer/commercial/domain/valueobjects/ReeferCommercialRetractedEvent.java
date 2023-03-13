package org.dcsa.reefer.commercial.domain.valueobjects;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
final public class ReeferCommercialRetractedEvent extends ReeferCommercialEvent {
  @Size(max = 100)
  private String retractedEventID;
}
