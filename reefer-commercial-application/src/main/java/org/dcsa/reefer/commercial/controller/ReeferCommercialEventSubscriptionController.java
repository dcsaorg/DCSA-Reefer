package org.dcsa.reefer.commercial.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription_;
import org.dcsa.reefer.commercial.service.ReeferCommercialEventSubscriptionService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionCreateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateSecretRequestTO;
import org.dcsa.skernel.infrastructure.pagination.Pagination;
import org.dcsa.skernel.infrastructure.sorting.Sorter.SortableFields;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("${spring.application.context-path}")
@RequiredArgsConstructor
public class ReeferCommercialEventSubscriptionController {
  private final List<Order> defaultSort = List.of(new Sort.Order(Sort.Direction.ASC, ReeferCommercialEventSubscription_.CREATED_DATE_TIME));
  private final SortableFields sortableFields =
    SortableFields.of(ReeferCommercialEventSubscription_.CREATED_DATE_TIME, ReeferCommercialEventSubscription_.UPDATED_DATE_TIME);

  private final ReeferCommercialEventSubscriptionService service;

  @GetMapping(path = "/event-subscriptions")
  @ResponseStatus(HttpStatus.OK)
  public List<ReeferCommercialEventSubscriptionTO> findSubscriptions(
    @RequestParam(value = Pagination.DCSA_PAGE_PARAM_NAME, defaultValue = "0", required = false) @Min(0)
    int page,

    @RequestParam(value = Pagination.DCSA_PAGESIZE_PARAM_NAME, defaultValue = "100", required = false) @Min(1)
    int pageSize,

    @RequestParam(value = Pagination.DCSA_SORT_PARAM_NAME, required = false)
    String sort,

    HttpServletRequest request, HttpServletResponse response
  ) {
    return Pagination
      .with(request, response, page, pageSize)
      .sortBy(sort, defaultSort, sortableFields)
      .paginate(service::findSubscriptions);
  }

  @GetMapping(path = "/event-subscriptions/{subscriptionID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialEventSubscriptionTO findSubscription(@PathVariable("subscriptionID") UUID subscriptionID) {
    return service.findSubscription(subscriptionID);
  }

  @PostMapping(path = "/event-subscriptions")
  @ResponseStatus(HttpStatus.CREATED)
  public ReeferCommercialEventSubscriptionTO createSubscription(
    @Valid @RequestBody ReeferCommercialEventSubscriptionCreateRequestTO request
  ) {
    return service.createSubscription(request);
  }

  @PutMapping(path = "/event-subscriptions/{subscriptionID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialEventSubscriptionTO updateSubscription(
    @PathVariable("subscriptionID") UUID subscriptionID,
    @Valid @RequestBody ReeferCommercialEventSubscriptionUpdateRequestTO request
  ) {
    return service.updateSubscription(subscriptionID, request);
  }

  @PutMapping(path = "/event-subscriptions/{subscriptionID}/secret")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateSubscriptionSecret(
    @PathVariable("subscriptionID") UUID subscriptionID,
    @Valid @RequestBody ReeferCommercialEventSubscriptionUpdateSecretRequestTO request
  ) {
    service.updateSubscriptionSecret(subscriptionID, request);
  }

  @DeleteMapping(path = "/event-subscriptions/{subscriptionID}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSubscription(@PathVariable("subscriptionID") UUID subscriptionID) {
    service.deleteSubscription(subscriptionID);
  }
}
