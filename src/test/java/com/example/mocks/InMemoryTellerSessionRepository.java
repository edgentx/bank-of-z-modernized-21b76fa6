package com.example.mocks;

import com.example.domain.uinavigation.model.TellerSessionAggregate;
import com.example.domain.uinavigation.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for TellerSessionAggregate used in testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public TellerSessionAggregate findById(String id) {
        return store.get(id);
    }

    public void clear() {
        store.clear();
    }
}
