package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSessionAggregate findById(String id) {
        return store.get(id);
    }
}