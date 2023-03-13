package org.dcsa.reefer.commercial.controller.unofficial;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.service.unofficial.UnofficialReeferCommercialEventService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("${spring.application.context-path}/unofficial")
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventController {
  private final UnofficialReeferCommercialEventService reeferCommercialEventService;

  @PostMapping(path = "/events/")
  @ResponseStatus(HttpStatus.CREATED)
  public void createEvent(@Valid @RequestBody ReeferCommercialEventTO eventTO) {
    reeferCommercialEventService.saveReeferCommercialEvent(eventTO);
  }
}
