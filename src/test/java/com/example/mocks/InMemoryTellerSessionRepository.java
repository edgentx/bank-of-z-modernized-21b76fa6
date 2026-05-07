package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        // In a real repository, we'd handle versioning here.
        // For tests, we just put it in the map.
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public TellerSessionAggregate findById(String id) {
        return store.get(id);
    }

    // Helper for tests to create a fresh aggregate without persisting first if needed,
    // though standard flow is usually new -> save -> load.
    public TellerSessionAggregate create(String sessionId) {
        return new TellerSessionAggregate(sessionId);
    }
}