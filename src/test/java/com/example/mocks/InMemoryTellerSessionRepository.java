package com.example.mocks;

import com.example.domain.shared.Aggregate;
import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// A simple in-memory repository for testing purposes.
public class InMemoryTellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerSessionAggregate load(String id) {
        // In a real event-sourced repo, we'd replay events. Here we just return the instance.
        return Optional.ofNullable(store.get(id)).orElseThrow(() -> new IllegalArgumentException("Session not found: " + id));
    }

    public TellerSessionAggregate create(String id) {
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        store.put(id, aggregate);
        return aggregate;
    }
}
