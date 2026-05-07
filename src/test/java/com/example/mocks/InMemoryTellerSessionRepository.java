package com.example.mocks;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for testing Teller Session logic.
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

    @Override
    public TellerSessionAggregate loadOrCreate(String id) {
        return store.computeIfAbsent(id, TellerSessionAggregate::new);
    }
}
