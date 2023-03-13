package org.dcsa.reefer.commercial.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.EventType;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.PublisherRole;

import java.time.OffsetDateTime;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract sealed class ReeferCommercialEvent permits ReeferCommercialPayloadEvent, ReeferCommercialRetractedEvent {
  @NotBlank @Size(max = 100)
  private String eventID;

  @NotNull
  private EventType eventType;

  @NotNull
  private OffsetDateTime eventCreatedDateTime;

  @NotNull @Valid
  private EventPublisher publisher;

  @NotNull
  private PublisherRole publisherRole;
}
