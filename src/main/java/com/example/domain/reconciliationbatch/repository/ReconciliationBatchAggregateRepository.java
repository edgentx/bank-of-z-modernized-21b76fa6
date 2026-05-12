package com.example.domain.reconciliationbatch.repository;

import com.example.domain.reconciliationbatch.model.ReconciliationBatchAggregate;
import java.util.Optional;

/**
 * Domain port for the canonical {@link ReconciliationBatchAggregate}
 * (reconciliation-and-controls bounded context). Named with the {@code Aggregate}
 * suffix so it does not collide with the older
 * {@code domain.reconciliation.repository.ReconciliationBatchRepository} which
 * targets a different (pre-scaffold) data class.
 */
public interface ReconciliationBatchAggregateRepository {
  Optional<ReconciliationBatchAggregate> findById(String batchId);
  void save(ReconciliationBatchAggregate aggregate);
}
