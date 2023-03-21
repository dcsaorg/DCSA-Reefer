package org.dcsa.reefer.commercial.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.OutgoingReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.entity.UnmatchedReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.OutgoingReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.UnmatchedReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSubscriptionSpecification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReeferCommercialEventMatchingService {
  private final UnmatchedReeferCommercialEventRepository unmatchedRepository;
  private final ReeferCommercialEventSubscriptionRepository subscriptionRepository;
  private final ReeferCommercialEventRepository eventRepository;
  private final OutgoingReeferCommercialEventRepository outgoingRepository;
  private final TransactionTemplate transactionTemplate;

  @Scheduled(
    initialDelayString = "${dcsa.reefer-commercial.match.initial-delay:5}",
    fixedDelayString = "${dcsa.reefer-commercial.match.fixed-delay:10}",
    timeUnit = TimeUnit.SECONDS
  )
  public void matchEventsScheduled() {
    // Continue until all has been processed
    while (Boolean.TRUE.equals(transactionTemplate.execute(this::matchEvents)));
  }

  private Boolean matchEvents(TransactionStatus transactionStatus) {
    Map<String, UnmatchedReeferCommercialEvent> unmatchedEvents = unmatchedRepository.findNext(5).stream()
      .collect(Collectors.toMap(UnmatchedReeferCommercialEvent::getEventId, Function.identity()));

    if (!unmatchedEvents.isEmpty()) {
      eventRepository.findAllById(unmatchedEvents.keySet()).forEach(event -> {
        UnmatchedReeferCommercialEvent unmatchedEvent = unmatchedEvents.get(event.getEventId());

        List<ReeferCommercialEventSubscription> subscriptions =
          subscriptionRepository.findAll(ReeferCommercialEventSubscriptionSpecification.matchesEvent(event));
        log.debug("ReeferCommercialEvent {} matched to subscriptions {}", event.getEventId(), subscriptions);

        outgoingRepository.saveAll(
          subscriptions.stream()
            .map(sub -> OutgoingReeferCommercialEvent.builder()
              .eventId(event.getEventId())
              .subscriptionId(sub.getId())
              .nextDeliveryAttemptTime(unmatchedEvent.getCreatedDateTime())
              .deliveryAttempts(0)
              .build())
            .toList()
        );

        unmatchedRepository.delete(unmatchedEvent);
      });
      return true;
    }
    return false;
  }
}
