package org.dcsa.reefer.commercial.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSpecification;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSpecification.ReeferCommercialEventFilters;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventMapper;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReeferCommercialEventService {
  private final ReeferCommercialEventRepository reeferCommercialEventRepository;
  private final ReeferCommercialEventMapper reeferCommercialEventMapper;

  @Transactional
  public ReeferCommercialEventTO findEvent(String eventID) {
    return reeferCommercialEventMapper.toDTO(
      reeferCommercialEventRepository.findById(eventID)
        .map(ReeferCommercialEvent::getContent)
        .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial events found for id = " + eventID)));
  }

  @Transactional
  public PagedResult<ReeferCommercialEventTO> findEvents(PageRequest pageRequest, ReeferCommercialEventFilters filters) {
    return new PagedResult<>(
      reeferCommercialEventRepository.findAll(ReeferCommercialEventSpecification.withFilters(filters), pageRequest),
      event -> reeferCommercialEventMapper.toDTO(event.getContent())
    );
  }
}
