package com.example.domain.reconciliation.repository;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;

public interface ReconciliationBatchRepository {
    ReconciliationBatchAggregate save(ReconciliationBatchAggregate aggregate);
    ReconciliationBatchAggregate findById(String id);
}
