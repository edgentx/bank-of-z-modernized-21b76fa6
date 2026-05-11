package com.example.mocks;

import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.repository.ScreenMapRepository;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for ScreenMapRepository.
 * Used in tests to avoid real database dependencies.
 */
public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public ScreenMapAggregate load(String id) {
        // In a real scenario, we might hydrate the state from events.
        // For simple mocking, we return the instance or null.
        return store.get(id);
    }

    @Override
    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
