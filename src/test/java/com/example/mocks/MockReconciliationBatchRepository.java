package com.example.mocks;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of ReconciliationBatchRepository for testing.
 */
public class InMemoryReconciliationBatchRepository implements ReconciliationBatchRepository {

    private final Map<String, ReconciliationBatch> store = new HashMap<>();

    @Override
    public Optional<ReconciliationBatch> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(ReconciliationBatch aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}