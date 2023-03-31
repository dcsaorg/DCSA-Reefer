package org.dcsa.reefer.commercial.delivery.service.exception;

final public class UnrecoverableEventDeliveryException extends RuntimeException {
  public UnrecoverableEventDeliveryException(String reason) {
    super(reason);
  }
}
