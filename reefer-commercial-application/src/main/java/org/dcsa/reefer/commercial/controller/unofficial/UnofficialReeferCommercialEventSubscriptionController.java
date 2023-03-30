package org.dcsa.reefer.commercial.controller.unofficial;

import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.service.unofficial.UnofficialReeferCommercialEventSubscriptionService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateSecretRequestTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Profile({"dev","test"})
@Validated
@RestController
@RequestMapping("${spring.application.context-path}/unofficial")
@RequiredArgsConstructor
public class UnofficialReeferCommercialEventSubscriptionController {
  private final UnofficialReeferCommercialEventSubscriptionService service;

  @GetMapping(path = "/event-subscriptions/{subscriptionID}/secret")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialEventSubscriptionUpdateSecretRequestTO findSecret(@PathVariable("subscriptionID") UUID subscriptionID) {
    return service.findSecret(subscriptionID);
  }
}
