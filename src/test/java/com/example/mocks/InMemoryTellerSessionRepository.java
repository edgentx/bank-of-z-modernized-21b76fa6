package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory implementation of TellerSessionRepository for testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        Objects.requireNonNull(aggregate, "aggregate cannot be null");
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSessionAggregate load(String id) {
        TellerSessionAggregate aggregate = store.get(id);
        if (aggregate == null) {
            throw new IllegalArgumentException("TellerSession not found: " + id);
        }
        return aggregate;
    }
}