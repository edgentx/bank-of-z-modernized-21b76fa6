package com.example.mocks;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;

import java.util.HashMap;
import java.util.Map;

public class ReconciliationBatchRepositoryMock implements ReconciliationBatchRepository {
    private final Map<String, ReconciliationBatchAggregate> store = new HashMap<>();

    @Override
    public ReconciliationBatchAggregate save(ReconciliationBatchAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public ReconciliationBatchAggregate findById(String id) {
        return store.get(id);
    }
}
