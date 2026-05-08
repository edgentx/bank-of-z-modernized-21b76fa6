package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the ScreenMapRepository for testing.
 */
public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ScreenMapAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
