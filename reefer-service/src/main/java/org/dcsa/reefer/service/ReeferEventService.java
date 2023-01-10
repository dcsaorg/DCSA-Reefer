package org.dcsa.reefer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dcsa.reefer.domain.persistence.entity.EventCache;
import org.dcsa.reefer.domain.persistence.repository.EventCacheRepository;
import org.dcsa.reefer.domain.persistence.repository.specification.EventCacheSpecification;
import org.dcsa.reefer.domain.persistence.repository.specification.EventCacheSpecification.EventCacheFilters;
import org.dcsa.reefer.transferobjects.ReeferEventTO;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReeferEventService {
  private final EventCacheRepository eventCacheRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public ReeferEventTO findEvent(UUID eventID) {
    return null;
  }

  @Transactional
  public PagedResult<ReeferEventTO> findEvents(PageRequest pageRequest, EventCacheFilters filters) {
    return new PagedResult<>(
      eventCacheRepository.findAll(EventCacheSpecification.withFilters(filters), pageRequest),
      this::deserializeEvent);
  }

  @SneakyThrows
  private ReeferEventTO deserializeEvent(EventCache event) {
    return objectMapper.readValue(event.getContent(), ReeferEventTO.class);
  }
}
