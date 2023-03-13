package org.dcsa.reefer.commercial.domain.persistence.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.ReeferEventTypeCode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "reefer_commercial_event")
public class ReeferCommercialEvent {
  @Id
  @Column(name = "event_id", nullable = false, length = 100)
  private String eventId;

  @Type(JsonBinaryType.class)
  @Column(name = "content", columnDefinition = "jsonb", nullable = false)
  private org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent content;

  @Column(name = "event_created_date_time", nullable = false)
  private OffsetDateTime eventCreatedDateTime;

  @Column(name = "event_date_time", nullable = false)
  private OffsetDateTime eventDateTime;

  @Formula("content->>'reeferEventTypeCode'")
  private String reeferEventTypeCode;

  @Formula("content->>'equipmentReference'")
  private String equipmentReference;
}
