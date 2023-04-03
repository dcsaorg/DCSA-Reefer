package org.dcsa.reefer.commercial.service;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSpecification.ReeferCommercialEventFilters;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialPayloadEvent;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.EventClassifierCode;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.ReeferEventTypeCode;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventMapper;
import org.dcsa.reefer.commercial.transferobjects.EventMetadataTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventPayloadTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.reefer.commercial.transferobjects.enums.EventType;
import org.dcsa.skernel.errors.exceptions.NotFoundException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReeferCommercialEventServiceTest {
  @Mock
  private ReeferCommercialEventRepository repository;

  @Spy
  private ReeferCommercialEventMapper mapper = Mappers.getMapper(ReeferCommercialEventMapper.class);

  @InjectMocks
  private ReeferCommercialEventService service;

  @Test
  public void testFindEvent() {
    when(repository.findById(any(String.class)))
      .thenReturn(Optional.of(ReeferCommercialEvent.builder().content(reeferCommercialPayloadEvent()).build()));

    ReeferCommercialEventTO actual = service.findEvent(eventId);

    assertEquals(reeferCommercialEventTO(), actual);
  }

  @Test
  public void testFindEvent_NotFound() {
    when(repository.findById(any(String.class))).thenReturn(Optional.empty());
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findEvent("test"));
    assertEquals("No reefer-commercial events found for id = test", exception.getMessage());
  }

  @Test
  public void testFindEvents() {
    when(repository.findAll(any(Specification.class), any(Pageable.class)))
      .thenReturn(new PageImpl(List.of(ReeferCommercialEvent.builder()
        .content(reeferCommercialPayloadEvent())
        .build())));

    PagedResult<ReeferCommercialEventTO> actual =
      service.findEvents(PageRequest.of(0, 10), ReeferCommercialEventFilters.builder().build());

    assertEquals(1, actual.content().size());
    assertEquals(reeferCommercialEventTO(), actual.content().get(0));
  }

  private String eventId = UUID.randomUUID().toString();
  private OffsetDateTime now = OffsetDateTime.now();

  private ReeferCommercialPayloadEvent reeferCommercialPayloadEvent() {
    return ReeferCommercialPayloadEvent.builder()
      .eventID(eventId)
      .eventType(org.dcsa.reefer.commercial.domain.valueobjects.enums.EventType.REEFER)
      .eventClassifierCode(EventClassifierCode.ACT)
      .eventDateTime(now)
      .reeferEventTypeCode(ReeferEventTypeCode.ADJU)
      .equipmentReference("MSKU9070323")
      .build();
  }

  private ReeferCommercialEventTO reeferCommercialEventTO() {
    return ReeferCommercialEventTO.builder()
      .metadata(EventMetadataTO.builder()
        .eventID(eventId)
        .eventType(EventType.REEFER)
        .build())
      .payload(ReeferCommercialEventPayloadTO.builder()
        .eventDateTime(now)
        .eventClassifierCode(org.dcsa.reefer.commercial.transferobjects.enums.EventClassifierCode.ACT)
        .eventDateTime(now)
        .reeferEventTypeCode(org.dcsa.reefer.commercial.transferobjects.enums.ReeferEventTypeCode.ADJU)
        .equipmentReference("MSKU9070323")
        .build())
      .build();
  }
}
