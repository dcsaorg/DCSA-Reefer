package org.dcsa.reefer.commercial.delivery.persistence.entity;

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
@Table(name = "delivered_event_message")
public class DeliveredEventMessage {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "event_id", nullable = false, length = 100)
  private String eventId;

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @Column(name = "callback_url", nullable = false)
  private String callbackUrl;

  @Column(name = "delivery_time", nullable = false)
  private OffsetDateTime deliveryTime;

  @Column(name = "delivery_attempts", nullable = false)
  private Integer deliveryAttempts;
}
