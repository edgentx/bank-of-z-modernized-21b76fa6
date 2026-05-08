package com.example.mocks;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of ScreenMapRepository for testing.
 */
public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<ScreenMapAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ScreenMapAggregate load(String id) {
        ScreenMapAggregate aggregate = store.get(id);
        if (aggregate == null) {
            // For testing purposes, we might want to auto-create or throw.
            // Here we mimic a JPA-like behavior where we assume we loaded it,
            // but for the Cucumber tests, we'll handle save/load flow explicitly.
            // However, to support the 'load' command simply:
            throw new IllegalArgumentException("ScreenMap not found: " + id);
        }
        return aggregate;
    }
}