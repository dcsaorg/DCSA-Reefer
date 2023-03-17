package org.dcsa.reefer.commercial.domain.persistence.entity;

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
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "reefer_commercial_event_subscription")
public class ReeferCommercialEventSubscription {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "callback_url", columnDefinition = "text", nullable = false)
  private String callbackUrl;

  @Column(name = "equipment_reference", length = 11)
  private String equipmentReference;

  @Column(name = "carrier_booking_reference", length = 35)
  private String carrierBookingReference;

  @ToString.Exclude
  @Column(name = "secret", columnDefinition = "bytea", nullable = false)
  private byte[] secret;

  @CreatedDate
  @Column(name = "created_date_time", nullable = false)
  private OffsetDateTime createdDateTime;

  @Column(name = "updated_date_time", nullable = false)
  private OffsetDateTime updatedDateTime;
}
