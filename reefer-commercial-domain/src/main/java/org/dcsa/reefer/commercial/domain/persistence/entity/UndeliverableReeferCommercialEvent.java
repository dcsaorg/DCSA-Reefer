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
import java.util.UUID;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "undeliverable_reefer_commercial_event")
public class UndeliverableReeferCommercialEvent {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "event_id", nullable = false, length = 100)
  private String eventId;

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @Column(name = "callback_url")
  private String callbackUrl;

  @Column(name = "last_delivery_attempt", nullable = false)
  private OffsetDateTime lastDeliveryAttemptTime;

  @Column(name = "delivery_attempts", nullable = false)
  private Integer deliveryAttempts;

  @Column(name = "error_details", nullable = false)
  private String errorDetails;
}
