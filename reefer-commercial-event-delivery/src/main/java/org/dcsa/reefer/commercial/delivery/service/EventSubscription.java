package org.dcsa.reefer.commercial.delivery.service;

import lombok.Builder;

import java.util.UUID;

public record EventSubscription(
  UUID subscriptionId,
  String callbackUrl,
  byte[] secret
) {
  @Builder(toBuilder = true)
  public EventSubscription { }
}
