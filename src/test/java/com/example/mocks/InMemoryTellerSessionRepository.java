package com.example.mocks;

import com.example.domain.tellermenu.model.TellerSessionAggregate;
import com.example.domain.tellermenu.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TellerSessionRepository for testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
