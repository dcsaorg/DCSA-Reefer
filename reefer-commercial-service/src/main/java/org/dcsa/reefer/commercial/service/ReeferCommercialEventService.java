package org.dcsa.reefer.commercial.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dcsa.reefer.commercial.domain.persistence.entity.EventCache;
import org.dcsa.reefer.commercial.domain.persistence.repository.EventCacheRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.EventCacheSpecification;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.EventCacheSpecification.EventCacheFilters;
import org.dcsa.reefer.commercial.service.domain.ReeferCommercialDomainEvent;
import org.dcsa.reefer.commercial.service.mapping.domain.ReeferCommercialDomainEventMapper;
import org.dcsa.reefer.commercial.service.mapping.transferobject.ReeferCommercialEventTOMapper;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReeferCommercialEventService {
  private final EventCacheRepository eventCacheRepository;
  private final ReeferCommercialEventTOMapper reeferCommercialEventTOMapper;
  private final ReeferCommercialDomainEventMapper reeferCommercialDomainEventMapper;
  private final ObjectMapper objectMapper;

  @Transactional
  public ReeferCommercialEventTO findEvent(UUID eventID) {
    return reeferCommercialEventTOMapper.toDTO(
      deserializeEvent(eventCacheRepository.findById(eventID)
        .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial events found for id = " + eventID))));
  }

  @Transactional
  public PagedResult<ReeferCommercialEventTO> findEvents(PageRequest pageRequest, EventCacheFilters filters) {
    return new PagedResult<>(
      eventCacheRepository.findAll(EventCacheSpecification.withFilters(filters), pageRequest),
      eventCache -> reeferCommercialEventTOMapper.toDTO(deserializeEvent(eventCache)));
  }

  @Transactional
  public ReeferCommercialDomainEvent findDomainEvent(UUID eventID) {
    throw ConcreteRequestErrorMessageException.notFound("No reefer events found for id = " + eventID);
    // TODO fetch event from db and convert it to ReeferCommercialDomainEvent (not from cache)
    // return reeferCommercialDomainEventMapper.toDomain(reeferCommercialEventRepository.findById(eventId)
    //    .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial events found for id = " + eventID))));
  }

  @Transactional
  public List<ReeferCommercialDomainEvent> findDomainEvents() {
    return Collections.emptyList();
    // TODO fetch events from db and convert them to ReeferCommercialDomainEvent (not from cache)
    // return reeferCommercialEventRepository.findAll().stream()
    //    .map(reeferCommercialDomainEventMapper::toDomain)
    //    .toList();
  }

  @SneakyThrows
  private ReeferCommercialDomainEvent deserializeEvent(EventCache event) {
    return objectMapper.readValue(event.getContent(), ReeferCommercialDomainEvent.class);
  }
}
