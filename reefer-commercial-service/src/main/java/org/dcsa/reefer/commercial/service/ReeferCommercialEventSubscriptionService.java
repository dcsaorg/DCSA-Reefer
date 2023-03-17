package org.dcsa.reefer.commercial.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventSubscriptionMapper;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionCreateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateSecretRequestTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReeferCommercialEventSubscriptionService {
  private final ReeferCommercialEventSubscriptionRepository repository;
  private final ReeferCommercialEventSubscriptionMapper mapper;

  @Transactional
  public PagedResult<ReeferCommercialEventSubscriptionTO> findSubscriptions(PageRequest pageRequest) {
    return new PagedResult<>(repository.findAll(pageRequest), mapper::toDTO);
  }

  @Transactional
  public ReeferCommercialEventSubscriptionTO findSubscription(UUID subscriptionID) {
    return repository.findById(subscriptionID)
      .map(mapper::toDTO)
      .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial-event-subscriptions found for subscriptionID = " + subscriptionID));
  }

  @Transactional
  public ReeferCommercialEventSubscriptionTO createSubscription(ReeferCommercialEventSubscriptionCreateRequestTO subscription) {
    return mapper.toDTO(repository.save(mapper.toDAO(subscription, OffsetDateTime.now())));
  }

  @Transactional
  public ReeferCommercialEventSubscriptionTO updateSubscription(UUID subscriptionID, ReeferCommercialEventSubscriptionUpdateRequestTO request) {
    return repository.findById(subscriptionID)
      .map(subscription -> subscription.toBuilder()
        .callbackUrl(request.getCallbackUrl())
        .equipmentReference(request.getEquipmentReference())
        .carrierBookingReference(request.getCarrierBookingReference())
        .updatedDateTime(OffsetDateTime.now())
        .build())
      .map(repository::save)
      .map(mapper::toDTO)
      .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial-event-subscriptions found for subscriptionID = " + subscriptionID));
  }

  @Transactional
  public void updateSubscriptionSecret(UUID subscriptionID, ReeferCommercialEventSubscriptionUpdateSecretRequestTO request) {
    repository.findById(subscriptionID)
      .map(subscription -> subscription.toBuilder()
        .secret(request.getSecret())
        .updatedDateTime(OffsetDateTime.now())
        .build())
      .map(repository::save)
      .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial-event-subscriptions found for subscriptionID = " + subscriptionID));
  }

  @Transactional
  public void deleteSubscription(UUID subscriptionID) {
    repository.deleteById(subscriptionID);
  }
}
