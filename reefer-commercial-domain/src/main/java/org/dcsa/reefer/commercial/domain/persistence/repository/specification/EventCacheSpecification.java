package org.dcsa.reefer.commercial.domain.persistence.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.EventCache;
import org.dcsa.reefer.commercial.domain.persistence.entity.EventCache_;
import org.dcsa.reefer.commercial.domain.persistence.entity.enums.EventType;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class EventCacheSpecification {

  public record EventCacheFilters(
    List<ParsedQueryParameter<OffsetDateTime>> eventCreatedDateTime,
    List<ParsedQueryParameter<OffsetDateTime>> eventDateTime
  ) {
    @Builder
    public EventCacheFilters { }
  }

  public static Specification<EventCache> withFilters(final EventCacheFilters filters) {
    log.debug("Searching based on {}", filters);

    return (Root<EventCache> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(builder.equal(root.get(EventCache_.EVENT_TYPE), EventType.REEFER_COMMERCIAL));

      handleParsedQueryParameter(
        predicates,
        builder,
        root.get(EventCache_.EVENT_CREATED_DATE_TIME),
        filters.eventCreatedDateTime
      );

      handleParsedQueryParameter(
        predicates,
        builder,
        root.get(EventCache_.EVENT_DATE_TIME),
        filters.eventDateTime
      );

      return builder.and(predicates.toArray(Predicate[]::new));
    };
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

  private static <T extends Comparable<T>> Predicate processParsedQueryParameter(CriteriaBuilder builder,
                                                                                 Expression<T> field,
                                                                                 ParsedQueryParameter<T> parsedQueryParameter) {
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
