package org.dcsa.reefer.commercial.delivery.persistence.entity;

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

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "outgoing_event_message")
public class OutgoingEventMessage {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "event_id", nullable = false, length = 100)
  private String eventId;

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @Column(name = "next_delivery_attempt", nullable = false)
  private OffsetDateTime nextDeliveryAttemptTime;

  @Column(name = "delivery_attempts", nullable = false)
  private Integer deliveryAttempts;

  /**
   * For convenience.
   */
  public static OutgoingEventMessage of(UUID subscriptionId, String eventId) {
    return OutgoingEventMessage.builder()
      .subscriptionId(subscriptionId)
      .eventId(eventId)
      .deliveryAttempts(0)
      .nextDeliveryAttemptTime(OffsetDateTime.now())
      .build();
  }
}
