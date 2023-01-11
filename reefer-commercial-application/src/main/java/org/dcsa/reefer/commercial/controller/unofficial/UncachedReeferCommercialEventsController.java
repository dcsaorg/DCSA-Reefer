package org.dcsa.reefer.commercial.controller.unofficial;

import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.service.ReeferCommercialEventService;
import org.dcsa.reefer.commercial.service.domain.ReeferCommercialDomainEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Just for testing and local development.
 */
@Profile({"test", "dev"})
@RestController
@RequestMapping("${spring.application.context-path}/unofficial/uncached-domain")
@RequiredArgsConstructor
public class UncachedReeferCommercialEventsController {
  private final ReeferCommercialEventService reeferCommercialEventService;

  @GetMapping(path = "/events/{eventID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialDomainEvent findEvent(@PathVariable("eventID") UUID eventID) {
    return reeferCommercialEventService.findDomainEvent(eventID);
  }

  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<ReeferCommercialDomainEvent> findEvents() {
    return reeferCommercialEventService.findDomainEvents();
  }
}
