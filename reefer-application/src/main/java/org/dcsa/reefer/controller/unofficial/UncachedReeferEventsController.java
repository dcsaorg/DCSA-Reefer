package org.dcsa.reefer.controller.unofficial;

import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.service.ReeferEventService;
import org.dcsa.reefer.service.domain.ReeferDomainEvent;
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
public class UncachedReeferEventsController {
  private final ReeferEventService reeferEventService;

  @GetMapping(path = "/events/{eventID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferDomainEvent findEvent(@PathVariable("eventID") UUID eventID) {
    return reeferEventService.findDomainEvent(eventID);
  }

  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<ReeferDomainEvent> findEvents() {
    return reeferEventService.findDomainEvents();
  }
}
