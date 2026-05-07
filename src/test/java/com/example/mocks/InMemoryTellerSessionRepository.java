package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String sessionId) {
        return Optional.ofNullable(store.get(sessionId));
    }

    @Override
    public TellerSessionAggregate create(String sessionId) {
        var aggregate = new TellerSessionAggregate(sessionId);
        store.put(sessionId, aggregate);
        return aggregate;
    }
}