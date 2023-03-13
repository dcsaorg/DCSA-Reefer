package org.dcsa.reefer.commercial.service.unofficial;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventDocumentReferenceRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.valueobjects.DocumentReference;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialPayloadEvent;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventService {
  private final ReeferCommercialEventRepository eventRepository;
  private final ReeferCommercialEventDocumentReferenceRepository documentReferenceRepository;
  private final UnofficialReeferCommercialEventMapper reeferCommercialEventMapper;

  @Transactional
  public void saveReeferCommercialEvent(ReeferCommercialEventTO eventTO) {
    ReeferCommercialEvent domainEvent = reeferCommercialEventMapper.toDomain(eventTO);
    eventRepository.save(toEntity(domainEvent));

    if (domainEvent instanceof ReeferCommercialPayloadEvent payloadEvent) {
      List<DocumentReference> relatedDocumentReferences = payloadEvent.getRelatedDocumentReferences();
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
