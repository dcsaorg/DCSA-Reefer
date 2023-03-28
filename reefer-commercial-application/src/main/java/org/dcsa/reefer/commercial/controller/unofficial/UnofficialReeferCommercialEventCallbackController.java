package org.dcsa.reefer.commercial.controller.unofficial;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.service.unofficial.UnofficialReeferCommercialEventService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.reefer.commercial.transferobjects.unofficial.ReeferCommercialEventStatusTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("${spring.application.context-path}/unofficial")
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventCallbackController {
  private final UnofficialReeferCommercialEventService reeferCommercialEventService;

  @PostMapping(path = "/callback/events/")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void receiveCallbackEvents(@Valid @RequestBody List<ReeferCommercialEventTO> eventTOs, HttpServletRequest request) {
    log.info("-- Incoming event");
    for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
      String headerName = headerNames.nextElement();
      for (Enumeration<String> headerValues = request.getHeaders(headerName); headerValues.hasMoreElements();) {
        log.info("Header - {}: {}", headerName, headerValues.nextElement());
      }
    }
    log.info("Received {}", eventTOs);
    log.info("-- end");
  }
}
