package org.dcsa.reefer.commercial.domain.persistence.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Builder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference_;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent_;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.DocumentReferenceType;
import org.dcsa.reefer.commercial.domain.valueobjects.enums.ReeferEventTypeCode;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.dcsa.reefer.commercial.domain.valueobjects.enums.DocumentReferenceType.BKG;
import static org.dcsa.reefer.commercial.domain.valueobjects.enums.DocumentReferenceType.CBR;

@Slf4j
@UtilityClass
public class ReeferCommercialEventSpecification {

  public static final Set<DocumentReferenceType> CARRIER_BOOKING_REF_TYPES = Set.of(BKG, CBR);

  public record ReeferCommercialEventFilters(
    List<ParsedQueryParameter<OffsetDateTime>> eventCreatedDateTime,
    List<ParsedQueryParameter<OffsetDateTime>> eventDateTime,
    Set<ReeferEventTypeCode> reeferEventTypeCodes,
    String equipmentReference,
    String carrierBookingReference
  ) {
    @Builder
    public ReeferCommercialEventFilters { }
  }

  public static Specification<ReeferCommercialEvent> withFilters(final ReeferCommercialEventFilters filters) {
    log.debug("Searching based on {}", filters);

    return (Root<ReeferCommercialEvent> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      handleParsedQueryParameter(
        predicates,
        builder,
        root.get(ReeferCommercialEvent_.EVENT_CREATED_DATE_TIME),
        filters.eventCreatedDateTime
      );

      handleParsedQueryParameter(
        predicates,
        builder,
        root.get(ReeferCommercialEvent_.EVENT_DATE_TIME),
        filters.eventDateTime
      );

      if (filters.equipmentReference != null) {
        predicates.add(builder.equal(root.get(ReeferCommercialEvent_.EQUIPMENT_REFERENCE), filters.equipmentReference));
      }

      if (filters.reeferEventTypeCodes != null && !filters.reeferEventTypeCodes.isEmpty()) {
        predicates.add(root.get(ReeferCommercialEvent_.REEFER_EVENT_TYPE_CODE).in(filters.reeferEventTypeCodes.stream().map(Enum::name).toList()));
      }

      handleDocumentReference(root, query, builder, predicates, CARRIER_BOOKING_REF_TYPES, filters.carrierBookingReference);

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static void handleDocumentReference(
    Root<ReeferCommercialEvent> root,
    CriteriaQuery<?> query,
    CriteriaBuilder builder,
    List<Predicate> predicates,
    Set<DocumentReferenceType> types,
    String reference
  ) {
    if (reference != null) {
      Subquery<ReeferCommercialEventDocumentReference> subQuery = query.subquery(ReeferCommercialEventDocumentReference.class);
      Root<ReeferCommercialEventDocumentReference> subRoot = subQuery.from(ReeferCommercialEventDocumentReference.class);
      subQuery.select(subRoot).where(
        builder.equal(root.get(ReeferCommercialEvent_.EVENT_ID), subRoot.get(ReeferCommercialEventDocumentReference_.EVENT_ID)),
        subRoot.get(ReeferCommercialEventDocumentReference_.TYPE).in(types),
        builder.equal(subRoot.get(ReeferCommercialEventDocumentReference_.VALUE), reference)
      );
      predicates.add(builder.exists(subQuery));
    }
  }

  private static <T extends Comparable<T>> void handleParsedQueryParameter(
    List<Predicate> predicates,
    CriteriaBuilder builder,
    Expression<T> field,
    List<ParsedQueryParameter<T>> filterValues
  ) {
    if (filterValues!= null && !filterValues.isEmpty()) {
      predicates.add(builder.or(filterValues.stream()
        .map(pqp -> processParsedQueryParameter(builder, field, pqp))
        .toArray(Predicate[]::new)
      ));
    }
  }

  private static <T extends Comparable<T>> Predicate processParsedQueryParameter(
    CriteriaBuilder builder,
    Expression<T> field,
    ParsedQueryParameter<T> parsedQueryParameter
  ) {
    final T value = parsedQueryParameter.value();
    return switch (parsedQueryParameter.comparisonType()) {
      case EQ -> builder.equal(field, value);
      case GTE -> builder.greaterThanOrEqualTo(field, value);
      case GT -> builder.greaterThan(field, value);
      case LTE -> builder.lessThanOrEqualTo(field, value);
      case LT -> builder.lessThan(field, value);
    };
  }
}
