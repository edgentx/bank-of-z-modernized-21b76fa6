package com.example.mocks;

import com.example.domain.shared.Aggregate;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for testing TellerSession.
 * Part of Mock Adapter pattern required for S-18 tests.
 */
public class MockTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSessionAggregate create(String id) {
        // Create a new instance and put it in store (transient)
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        store.put(id, agg);
        return agg;
    }

    public void clear() {
        store.clear();
    }
}