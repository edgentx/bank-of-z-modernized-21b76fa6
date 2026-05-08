package com.example.domain.screen.repository;

import com.example.domain.screen.model.ScreenMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 * Matches existing repository patterns in the codebase (e.g. CustomerRepository).
 */
public interface ScreenMapRepository {
    void save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}

/**
 * In-memory implementation for unit testing and Cucumber steps.
 * Located in the repository package file as a static inner class or helper
 * to adhere to single-file output constraints where possible, 
 * though typically this would be its own file.
 */
class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMap> store = new HashMap<>();

    @Override
    public void save(ScreenMap aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ScreenMap> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
