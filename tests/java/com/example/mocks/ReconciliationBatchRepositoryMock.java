package com.example.mocks;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReconciliationBatchRepositoryMock implements ReconciliationBatchRepository {
    private final Map<String, ReconciliationBatchAggregate> store = new HashMap<>();

    @Override
    public void save(ReconciliationBatchAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ReconciliationBatchAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
