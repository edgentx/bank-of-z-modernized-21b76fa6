package com.example.domain.reconciliation.repository;

import com.example.domain.reconciliation.model.ReconciliationBatch;

import java.util.Optional;

public interface ReconciliationBatchRepository {
    void save(ReconciliationBatch aggregate);
    Optional<ReconciliationBatch> findById(String id);
}