package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for ScreenMap aggregates.
 * Used exclusively for unit testing and Cucumber step definitions.
 */
public class InMemoryScreenMapRepository {

    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public ScreenMapAggregate findById(String id) {
        return store.get(id);
    }

    public Optional<ScreenMapAggregate> find(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(String id) {
        store.remove(id);
    }
}
