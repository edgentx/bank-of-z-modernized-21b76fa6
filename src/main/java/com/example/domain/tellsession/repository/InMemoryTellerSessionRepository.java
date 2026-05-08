package com.example.domain.tellsession.repository;

import com.example.domain.tellsession.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate load(String id) {
        TellerSessionAggregate aggregate = store.get(id);
        if (aggregate == null) {
            throw new IllegalArgumentException("No session found for id: " + id);
        }
        return aggregate;
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
