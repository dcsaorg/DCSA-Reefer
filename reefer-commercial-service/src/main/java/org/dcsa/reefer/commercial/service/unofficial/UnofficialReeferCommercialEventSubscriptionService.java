package org.dcsa.reefer.commercial.service.unofficial;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateSecretRequestTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Profile("test")
@Service
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventSubscriptionService {
  private final ReeferCommercialEventSubscriptionRepository repository;

  @Transactional
  public ReeferCommercialEventSubscriptionUpdateSecretRequestTO findSecret(UUID subscriptionID) {
    return repository.findById(subscriptionID)
      .map(subscription -> new ReeferCommercialEventSubscriptionUpdateSecretRequestTO(subscription.getSecret()))
      .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No reefer-commercial-event-subscriptions found for subscriptionID = " + subscriptionID));
  }
}
