package com.example.mocks;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockReconciliationBatchRepository implements ReconciliationBatchRepository {
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
