package org.dcsa.reefer.commercial.domain.persistence.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventDocumentReference_;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription_;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.dcsa.reefer.commercial.domain.persistence.repository.specification.ReeferCommercialEventSpecification.CARRIER_BOOKING_REF_TYPES;

@Slf4j
@UtilityClass
public class ReeferCommercialEventSubscriptionSpecification {
  public static Specification<ReeferCommercialEventSubscription> matchesEvent(ReeferCommercialEvent event) {
    return (Root<ReeferCommercialEventSubscription> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (event.getEquipmentReference() == null) {
        predicates.add(builder.isNull(root.get(ReeferCommercialEventSubscription_.EQUIPMENT_REFERENCE)));
      } else {
        predicates.add(
          builder.or(
            builder.isNull(root.get(ReeferCommercialEventSubscription_.EQUIPMENT_REFERENCE)),
            builder.equal(root.get(ReeferCommercialEventSubscription_.EQUIPMENT_REFERENCE), event.getEquipmentReference())
          )
        );
      }

      Subquery<ReeferCommercialEventDocumentReference> subQuery = query.subquery(ReeferCommercialEventDocumentReference.class);
      Root<ReeferCommercialEventDocumentReference> subRoot = subQuery.from(ReeferCommercialEventDocumentReference.class);
      subQuery.select(subRoot).where(
        builder.equal(subRoot.get(ReeferCommercialEventDocumentReference_.EVENT_ID), event.getEventId()),
        subRoot.get(ReeferCommercialEventDocumentReference_.TYPE).in(CARRIER_BOOKING_REF_TYPES),
        builder.equal(subRoot.get(ReeferCommercialEventDocumentReference_.VALUE), root.get(ReeferCommercialEventSubscription_.CARRIER_BOOKING_REFERENCE))
      );
      predicates.add(
        builder.or(
          builder.isNull(root.get(ReeferCommercialEventSubscription_.CARRIER_BOOKING_REFERENCE)),
          builder.exists(subQuery)
        )
      );

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
