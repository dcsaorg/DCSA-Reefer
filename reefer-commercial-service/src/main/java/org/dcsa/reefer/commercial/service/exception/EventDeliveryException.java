package org.dcsa.reefer.commercial.service.exception;

final public class EventDeliveryException extends RuntimeException {
  public EventDeliveryException(String reason) {
    super(reason);
  }

  public EventDeliveryException(Throwable cause) {
    super(cause.getClass().getSimpleName() + ": " +  cause.getMessage(), cause);
  }
}
