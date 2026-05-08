package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the ScreenMap Repository for testing.
 */
public class InMemoryScreenMapRepository implements ScreenMapRepository {

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
