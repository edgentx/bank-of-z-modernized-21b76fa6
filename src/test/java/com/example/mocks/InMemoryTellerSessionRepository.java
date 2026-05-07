package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the TellerSessionRepository for testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public TellerSessionAggregate create(String id) {
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        store.put(id, aggregate);
        return aggregate;
    }
}