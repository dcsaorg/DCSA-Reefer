package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.UnmatchedReeferCommercialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnmatchedReeferCommercialEventRepository extends JpaRepository<UnmatchedReeferCommercialEvent, String> {
  @Query(value = "SELECT * FROM unmatched_reefer_commercial_event ORDER BY created_date_time FOR UPDATE SKIP LOCKED LIMIT :limit", nativeQuery = true)
  List<UnmatchedReeferCommercialEvent> findNext(int limit);
}
