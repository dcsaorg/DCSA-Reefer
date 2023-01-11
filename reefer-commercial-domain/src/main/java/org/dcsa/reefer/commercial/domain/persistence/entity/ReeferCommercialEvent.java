package org.dcsa.reefer.commercial.domain.persistence.entity;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * TODO real entity when we have it
 */
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
// @Entity
// @Table(name = "reefer_commercial_event")
public class ReeferCommercialEvent {
  private UUID eventId;
  private OffsetDateTime eventCreatedDateTime;
  private OffsetDateTime eventDateTime;
}
