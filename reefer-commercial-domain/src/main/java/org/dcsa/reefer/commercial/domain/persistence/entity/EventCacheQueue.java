package org.dcsa.reefer.commercial.domain.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dcsa.reefer.commercial.domain.persistence.entity.enums.EventType;

import java.util.UUID;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "event_cache_queue")
public class EventCacheQueue {
  @Id
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private EventType eventType;
}
