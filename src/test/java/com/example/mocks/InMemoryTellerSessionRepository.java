package com.example.mocks;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TellerSessionRepository for testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        // Return a copy or handle instance management as needed. 
        // For aggregate testing, we usually want to load the instance, execute, and save.
        // This mock returns the exact instance.
        return Optional.ofNullable(store.get(id));
    }
}