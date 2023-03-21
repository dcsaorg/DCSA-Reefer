package org.dcsa.reefer.commercial.domain.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "unmatched_reefer_commercial_event")
public class UnmatchedReeferCommercialEvent {
  @Id
  @Column(name = "event_id", nullable = false, length = 100)
  private String eventId;

  @Column(name = "created_date_time", nullable = false)
  private OffsetDateTime createdDateTime;
}
