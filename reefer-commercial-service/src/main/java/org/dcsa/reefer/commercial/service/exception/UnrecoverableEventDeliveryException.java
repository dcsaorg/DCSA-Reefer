package org.dcsa.reefer.commercial.service.exception;

final public class UnrecoverableEventDeliveryException extends RuntimeException {
  public UnrecoverableEventDeliveryException(String reason) {
    super(reason);
  }

  public UnrecoverableEventDeliveryException(Throwable cause) {
    super(cause.getClass().getSimpleName() + ": " +  cause.getMessage(), cause);
  }
}
