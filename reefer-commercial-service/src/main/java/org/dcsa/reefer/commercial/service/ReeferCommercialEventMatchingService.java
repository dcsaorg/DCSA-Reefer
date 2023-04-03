package org.dcsa.reefer.commercial.service;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.delivery.persistence.entity.OutgoingEventMessage;
import org.dcsa.reefer.commercial.delivery.persistence.repository.OutgoingEventMessageRepository;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSubscriptionSpecification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReeferCommercialEventMatchingService {
  private final ReeferCommercialEventSubscriptionRepository subscriptionRepository;
  private final OutgoingEventMessageRepository outgoingRepository;

  @Transactional(TxType.MANDATORY)
  public void matchEvent(ReeferCommercialEvent event) {
    List<ReeferCommercialEventSubscription> subscriptions =
      subscriptionRepository.findAll(ReeferCommercialEventSubscriptionSpecification.matchesEvent(event));

    log.debug("Matched ReeferCommercialEvent '{}' to {} subscriptions", event.getEventId(), subscriptions.size());

    if (!subscriptions.isEmpty()) {
      outgoingRepository.saveAll(
        subscriptions.stream()
          .map(sub -> OutgoingEventMessage.of(sub.getId(), event.getEventId()))
          .toList()
      );
    }
  }
}
