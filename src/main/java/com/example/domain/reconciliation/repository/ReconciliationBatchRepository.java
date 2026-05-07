package com.example.domain.reconciliation.repository;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;

import java.util.Optional;

/**
 * Repository interface for ReconciliationBatch aggregates.
 */
public interface ReconciliationBatchRepository {
    void save(ReconciliationBatchAggregate aggregate);
    Optional<ReconciliationBatchAggregate> findById(String id);
}
