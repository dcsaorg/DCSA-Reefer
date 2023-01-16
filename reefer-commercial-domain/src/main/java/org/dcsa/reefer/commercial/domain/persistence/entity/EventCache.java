package org.dcsa.reefer.commercial.domain.persistence.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import org.dcsa.reefer.commercial.domain.persistence.entity.enums.EventType;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "event_cache")
public class EventCache {
  @Id
  @Column(name = "event_id", nullable = false)
  private UUID eventID;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private EventType eventType;

  @Type(JsonBinaryType.class)
  @Column(name = "content", columnDefinition = "jsonb", nullable = false)
  private String content;

  @CreatedDate
  @Column(name = "event_created_date_time", nullable = false)
  private OffsetDateTime eventCreatedDateTime;

  @Column(name = "event_date_time", nullable = false)
  private OffsetDateTime eventDateTime;
}
