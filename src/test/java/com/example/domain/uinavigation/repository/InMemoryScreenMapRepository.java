package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.ScreenMapAggregate;
import java.util.HashMap;
import java.util.Map;

// Implementation of InMemoryRepository pattern used in tests
public class InMemoryScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    public InMemoryScreenMapRepository() {
        // Pre-populate with the test aggregate used by S22Steps
        save(new ScreenMapAggregate("DEMO_SCREEN"));
    }

    public ScreenMapAggregate findById(String id) {
        return store.get(id);
    }

    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
