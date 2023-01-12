package org.dcsa.reefer.commercial.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.EventCache_;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.EventCacheSpecification.EventCacheFilters;
import org.dcsa.reefer.commercial.service.ReeferCommercialEventService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.skernel.infrastructure.http.queryparams.DCSAQueryParameterParser;
import org.dcsa.skernel.infrastructure.pagination.Pagination;
import org.dcsa.skernel.infrastructure.sorting.Sorter.SortableFields;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("${spring.application.context-path}")
@RequiredArgsConstructor
public class ReeferCommercialEventController {
  private final List<Order> defaultSort = List.of(new Sort.Order(Sort.Direction.ASC, EventCache_.EVENT_CREATED_DATE_TIME));
  private final SortableFields sortableFields = SortableFields.of(EventCache_.EVENT_CREATED_DATE_TIME, EventCache_.EVENT_DATE_TIME);

  private final ReeferCommercialEventService reeferCommercialEventService;
  private final DCSAQueryParameterParser queryParameterParser;

  @GetMapping(path = "/events/{eventID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialEventTO findEvent(@PathVariable("eventID") UUID eventID) {
    return reeferCommercialEventService.findEvent(eventID);
  }

  @GetMapping(path = "/events")
  @ResponseStatus(HttpStatus.OK)
  public List<ReeferCommercialEventTO> findEvents(
    @RequestParam(value = Pagination.DCSA_PAGE_PARAM_NAME, defaultValue = "0", required = false) @Min(0)
    int page,

    @RequestParam(value = Pagination.DCSA_PAGESIZE_PARAM_NAME, defaultValue = "100", required = false) @Min(1)
    int pageSize,

    @RequestParam(value = Pagination.DCSA_SORT_PARAM_NAME, required = false)
    String sort,

    @RequestParam
    Map<String, String> queryParams,

    HttpServletRequest request, HttpServletResponse response
  ) {
    return Pagination
      .with(request, response, page, pageSize)
      .sortBy(sort, defaultSort, sortableFields)
      .paginate(pageRequest ->
        reeferCommercialEventService.findEvents(pageRequest, EventCacheFilters.builder()
          .eventCreatedDateTime(queryParameterParser.parseCustomQueryParameter(queryParams, "eventCreatedDateTime", OffsetDateTime::parse))
          .eventDateTime(queryParameterParser.parseCustomQueryParameter(queryParams, "eventDateTime", OffsetDateTime::parse))
          .build()));
  }
}
