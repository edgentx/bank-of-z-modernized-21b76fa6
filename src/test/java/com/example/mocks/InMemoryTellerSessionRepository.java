package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate load(String sessionId) {
        return store.get(sessionId);
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
