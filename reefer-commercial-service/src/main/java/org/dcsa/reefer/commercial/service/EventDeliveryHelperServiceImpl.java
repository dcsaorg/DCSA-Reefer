package org.dcsa.reefer.commercial.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.dcsa.reefer.commercial.delivery.service.EventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventMapper;
import org.springframework.stereotype.Service;
import org.dcsa.reefer.commercial.delivery.service.EventDeliveryHelperService;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventDeliveryHelperServiceImpl implements EventDeliveryHelperService {
  private final ReeferCommercialEventRepository reeferCommercialEventRepository;
  private final ReeferCommercialEventSubscriptionRepository subscriptionRepository;
  private final ReeferCommercialEventMapper reeferCommercialEventMapper;

  @Override
  @Transactional
  public Optional<EventSubscription> findSubscriptionById(UUID subscriptionId) {
    return subscriptionRepository.findById(subscriptionId)
      .map(s -> EventSubscription.builder()
        .subscriptionId(s.getId())
        .callbackUrl(s.getCallbackUrl())
        .secret(s.getSecret())
        .build());
  }

  @Override
  @Transactional
  public Optional<Object> findEventByIdAsTO(String eventId) {
    return reeferCommercialEventRepository.findById(eventId)
      .map(ReeferCommercialEvent::getContent)
      .map(reeferCommercialEventMapper::toDTO);
  }
}
