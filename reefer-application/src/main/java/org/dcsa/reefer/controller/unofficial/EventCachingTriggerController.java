package org.dcsa.reefer.controller.unofficial;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.service.EventCachingService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Just for testing and local development.
 */
@Slf4j
@Profile({"test", "dev"})
@RestController
@RequestMapping("${spring.application.context-path}/unofficial")
@RequiredArgsConstructor
public class EventCachingTriggerController {
  private final EventCachingService eventCachingService;

  @PostMapping(value = "/events-cache-trigger")
  public void triggerCaching() {
    log.debug("Triggered event caching");
    eventCachingService.cacheEvents();
  }
}
