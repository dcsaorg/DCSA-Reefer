package org.dcsa.reefer.commercial.delivery.service.exception;

final public class EventDeliveryException extends RuntimeException {
  public EventDeliveryException(String reason) {
    super(reason);
  }
}
