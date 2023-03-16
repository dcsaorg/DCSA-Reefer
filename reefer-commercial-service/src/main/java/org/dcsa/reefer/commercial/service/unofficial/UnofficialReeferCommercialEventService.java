package org.dcsa.reefer.commercial.service.unofficial;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventDocumentReferenceRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.valueobjects.DocumentReference;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialPayloadEvent;
import org.dcsa.reefer.commercial.transferobjects.EventMetadataTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventPayloadTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.reefer.commercial.transferobjects.unofficial.ReeferCommercialEventStatusTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventService {
  private final ReeferCommercialEventRepository eventRepository;
  private final ReeferCommercialEventDocumentReferenceRepository documentReferenceRepository;
  private final UnofficialReeferCommercialEventMapper reeferCommercialEventMapper;

  @Transactional
  public ReeferCommercialEventStatusTO saveReeferCommercialEvent(ReeferCommercialEventTO eventTO) {
    EventMetadataTO metadata = eventTO.metadata();
    ReeferCommercialEventPayloadTO payload = eventTO.payload();

    if (metadata.retractedEventID() == null && payload == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("event should either have a retractedEventID or a payload");
    }
    if (metadata.retractedEventID() != null && payload != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("event should not have both a retractedEventID or a payload");
    }

    ReeferCommercialEventTO updated = eventTO.toBuilder()
        .metadata(metadata.toBuilder()
          .eventID(Objects.requireNonNullElseGet(metadata.eventID(), () -> UUID.randomUUID().toString()))
          .eventCreatedDateTime(Objects.requireNonNullElseGet(metadata.eventCreatedDateTime(), OffsetDateTime::now))
          .build())
      .payload(payload == null ? null : payload.toBuilder()
        .eventDateTime(Objects.requireNonNullElseGet(payload.eventDateTime(), OffsetDateTime::now))
        .build())
      .build();

    ReeferCommercialEvent domainEvent = reeferCommercialEventMapper.toDomain(updated);
    eventRepository.save(toEntity(domainEvent));

    if (domainEvent instanceof ReeferCommercialPayloadEvent payloadEvent) {
      Set<DocumentReference> relatedDocumentReferences = payloadEvent.getRelatedDocumentReferences();
      if (relatedDocumentReferences != null && !relatedDocumentReferences.isEmpty()) {
        eventRepository.flush();
        documentReferenceRepository.saveAll(
          relatedDocumentReferences.stream()
            .map(ref -> ReeferCommercialEventDocumentReference.builder()
              .eventId(domainEvent.getEventID())
              .type(ref.type())
              .value(ref.value())
              .build())
            .toList());
      }
    }

    return new ReeferCommercialEventStatusTO(domainEvent.getEventID());
  }

  private org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent toEntity(ReeferCommercialEvent domainEvent) {
    return org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent.builder()
      .eventId(domainEvent.getEventID())
      .content(domainEvent)
      .eventCreatedDateTime(domainEvent.getEventCreatedDateTime())
      .eventDateTime(domainEvent instanceof ReeferCommercialPayloadEvent payloadEvent ? payloadEvent.getEventDateTime() : null)
      .build();
  }
}
