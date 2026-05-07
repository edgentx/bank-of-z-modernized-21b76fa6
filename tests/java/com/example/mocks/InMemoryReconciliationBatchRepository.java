package com.example.mocks;
import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import java.util.*;
public class InMemoryReconciliationBatchRepository implements ReconciliationBatchRepository {
    private final Map<String, ReconciliationBatchAggregate> store = new HashMap<>();
    @Override public void save(ReconciliationBatchAggregate a) { store.put(a.id(), a); }
    @Override public Optional<ReconciliationBatchAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}