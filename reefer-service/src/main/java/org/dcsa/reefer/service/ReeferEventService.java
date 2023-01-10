package org.dcsa.reefer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dcsa.reefer.domain.persistence.entity.EventCache;
import org.dcsa.reefer.domain.persistence.repository.EventCacheRepository;
import org.dcsa.reefer.domain.persistence.repository.specification.EventCacheSpecification;
import org.dcsa.reefer.domain.persistence.repository.specification.EventCacheSpecification.EventCacheFilters;
import org.dcsa.reefer.service.domain.ReeferDomainEvent;
import org.dcsa.reefer.service.mapping.domain.ReeferDomainEventMapper;
import org.dcsa.reefer.service.mapping.transferobject.ReeferEventTOMapper;
import org.dcsa.reefer.transferobjects.ReeferEventTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReeferEventService {
  private final EventCacheRepository eventCacheRepository;
  private final ReeferEventTOMapper reeferEventTOMapper;
  private final ReeferDomainEventMapper reeferDomainEventMapper;
  private final ObjectMapper objectMapper;

  @Transactional
  public ReeferEventTO findEvent(UUID eventID) {
    return reeferEventTOMapper.toDTO(
      deserializeEvent(eventCacheRepository.findById(eventID)
        .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer events found for id = " + eventID))));
  }

  @Transactional
  public PagedResult<ReeferEventTO> findEvents(PageRequest pageRequest, EventCacheFilters filters) {
    return new PagedResult<>(
      eventCacheRepository.findAll(EventCacheSpecification.withFilters(filters), pageRequest),
      eventCache -> reeferEventTOMapper.toDTO(deserializeEvent(eventCache)));
  }

  @Transactional
  public ReeferDomainEvent findDomainEvent(UUID eventID) {
    throw ConcreteRequestErrorMessageException.notFound("No reefer events found for id = " + eventID);
    // TODO fetch event from db and convert it to ReeferDomainEvent (not from cache)
    // return reeferDomainEventMapper.toDomain(reeferEventRepository.findById(eventId)
    //    .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer events found for id = " + eventID))));
  }

  @Transactional
  public List<ReeferDomainEvent> findDomainEvents() {
    return Collections.emptyList();
    // TODO fetch events from db and convert them to ReeferDomainEvent (not from cache)
    // return reeferEventRepository.findAll().stream()
    //    .map(reeferDomainEventMapper::toDomain)
    //    .toList();
  }

  @SneakyThrows
  private ReeferDomainEvent deserializeEvent(EventCache event) {
    return objectMapper.readValue(event.getContent(), ReeferDomainEvent.class);
  }
}
