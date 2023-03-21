package org.dcsa.reefer.commercial.domain.persistence.repository;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReeferCommercialEventSubscriptionRepository
  extends JpaRepository<ReeferCommercialEventSubscription, UUID>, JpaSpecificationExecutor<ReeferCommercialEventSubscription> {

  /* Actual spring implementation throws exception if entity does not exist.
   * This restores the functionality as described in the documentation:
   *
   *    Deletes the entity with the given id.
   *    If the entity is not found in the persistence store it is silently ignored.
   */
  @Override
  @Modifying
  @Query("DELETE FROM ReeferCommercialEventSubscription where id = :id")
  void deleteById(UUID id);
}
