package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for testing ScreenMap aggregates.
 * Mirrors the structure of InMemoryTransactionRepository.
 */
public class InMemoryScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    public Optional<ScreenMapAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
