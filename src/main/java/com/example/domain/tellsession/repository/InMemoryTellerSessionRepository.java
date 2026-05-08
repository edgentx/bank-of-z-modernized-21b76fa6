package com.example.domain.tellsession.repository;

import com.example.domain.tellsession.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
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