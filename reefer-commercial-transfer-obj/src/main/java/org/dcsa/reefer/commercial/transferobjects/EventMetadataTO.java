package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dcsa.reefer.commercial.transferobjects.enums.EventType;
import org.dcsa.reefer.commercial.transferobjects.enums.PublisherRole;

import java.time.OffsetDateTime;

public record EventMetadataTO(
  @NotBlank @Size(max = 100)
  String eventID,

  @NotNull
  EventType eventType,

  @NotNull
  OffsetDateTime eventCreatedDateTime,

  @Size(max = 100)
  String retractedEventID,

  @NotNull @Valid
  EventPublisherTO publisher,

  @NotNull
  PublisherRole publisherRole
) {
  @Builder(toBuilder = true)
  public EventMetadataTO { }
}
