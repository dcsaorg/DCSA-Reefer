package org.dcsa.reefer.commercial.delivery.service;

import java.util.Optional;
import java.util.UUID;

public interface EventDeliveryHelperService {
  /**
   * Finds and returns an event subscription or an Optional.empty if no subscription is found..
   */
  Optional<EventSubscription> findSubscriptionById(UUID subscriptionId);

  /**
   * Finds and returns an event tranformed to the transfer object that is supposed to be sent or an Optional.empty if no event is found..
   */
  Optional<Object> findEventByIdAsTO(String eventId);
}
