package org.dcsa.reefer.commercial.controller.unofficial;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.delivery.service.EventDeliveryService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Profile({"dev","test"})
@Slf4j
@Validated
@RestController
@RequestMapping("${spring.application.context-path}/unofficial")
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventCallbackController {
  private final EventDeliveryService deliveryService;
  private final Map<String, Set<String>> receivedEvents = new HashMap<>();

  @PostMapping(path = "/callback/deliver-events/")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deliverEvents() {
    deliveryService.deliverBlocking();
  }

  @PostMapping(path = "/callback/events/clear")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearReceived() {
    synchronized (receivedEvents) {
      receivedEvents.clear();
    }
  }

  @PostMapping(path = "/callback/events/")
  public ResponseEntity<String> receiveCallbackEvents(@RequestBody @NotEmpty List<@Valid ReeferCommercialEventTO> events, HttpServletRequest request) {
    String subscriptionId = request.getHeader("Subscription-ID");
    if (subscriptionId == null) {
      return ResponseEntity.badRequest().body("No 'Subscription-ID' header");
    }
    registerEvents(subscriptionId, events.stream().map(e -> e.metadata().eventID()).collect(Collectors.toSet()));

    if (log.isTraceEnabled()) {
      log.trace("-- Incoming events");
      for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
        String headerName = headerNames.nextElement();
        for (Enumeration<String> headerValues = request.getHeaders(headerName); headerValues.hasMoreElements(); ) {
          log.trace("Header - {}: {}", headerName, headerValues.nextElement());
        }
      }
      log.trace("-- end");
    }

    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/callback/events/{subscriptionId}/")
  @ResponseStatus(HttpStatus.OK)
  public Set<String> getEventsForSubscription(@PathVariable("subscriptionId") String subscriptionId) {
    synchronized (receivedEvents) {
      return receivedEvents.getOrDefault(subscriptionId, Collections.emptySet());
    }
  }

  private void registerEvents(String subscriptionId, Set<String> eventIds) {
    log.info("Received subscription {}, events {}", subscriptionId, eventIds);
    synchronized (receivedEvents) {
      receivedEvents.computeIfAbsent(subscriptionId, id -> new HashSet<>()).addAll(eventIds);
    }
  }
}
