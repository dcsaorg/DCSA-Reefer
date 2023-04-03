package org.dcsa.reefer.commercial.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent_;
import org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSpecification.ReeferCommercialEventFilters;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.ReeferEventTypeCode;
import org.dcsa.reefer.commercial.service.ReeferCommercialEventService;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventTO;
import org.dcsa.skernel.infrastructure.http.queryparams.DCSAQueryParameterParser;
import org.dcsa.skernel.infrastructure.pagination.Pagination;
import org.dcsa.skernel.infrastructure.sorting.Sorter.SortableFields;
import org.dcsa.skernel.infrastructure.util.EnumUtil;
import org.dcsa.skernel.infrastructure.validation.ISO6346EquipmentReference;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Validated
@RestController
@RequestMapping("${spring.application.context-path}")
@RequiredArgsConstructor
public class ReeferCommercialEventController {
  private final List<Order> defaultSort = List.of(new Sort.Order(Sort.Direction.ASC, ReeferCommercialEvent_.EVENT_CREATED_DATE_TIME));
  private final SortableFields sortableFields = SortableFields.of(ReeferCommercialEvent_.EVENT_CREATED_DATE_TIME, ReeferCommercialEvent_.EVENT_DATE_TIME);

  private final ReeferCommercialEventService reeferCommercialEventService;
  private final DCSAQueryParameterParser queryParameterParser;

  @GetMapping(path = "/events/{eventID}")
  @ResponseStatus(HttpStatus.OK)
  public ReeferCommercialEventTO findEvent(@PathVariable("eventID") String eventID) {
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

    @Size(max = 35)
    @RequestParam(value = "carrierBookingReference", required = false)
    String carrierBookingReference,

    @Size(max = 11) @ISO6346EquipmentReference
    @RequestParam(value = "equipmentReference", required = false)
    String equipmentReference,

    @RequestParam(value = "reeferEventTypeCodes", required = false)
    String reeferEventTypeCodes,

    @RequestParam
    Map<String, String> queryParams,

    HttpServletRequest request, HttpServletResponse response
  ) {
    List<ReeferEventTypeCode> reeferEventTypeCodeList =
      Objects.requireNonNullElse(EnumUtil.toEnumList(reeferEventTypeCodes, ReeferEventTypeCode.class), Collections.emptyList());

    return Pagination
      .with(request, response, page, pageSize)
      .sortBy(sort, defaultSort, sortableFields)
      .paginate(pageRequest ->
        reeferCommercialEventService.findEvents(pageRequest, ReeferCommercialEventFilters.builder()
          .eventCreatedDateTime(queryParameterParser.parseCustomQueryParameter(queryParams, "eventCreatedDateTime", OffsetDateTime::parse))
          .eventDateTime(queryParameterParser.parseCustomQueryParameter(queryParams, "eventDateTime", OffsetDateTime::parse))
          .carrierBookingReference(carrierBookingReference)
          .equipmentReference(equipmentReference)
          .reeferEventTypeCodes(Set.copyOf(reeferEventTypeCodeList))
          .build()));
  }
}
