package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerSessionAggregate load(String id) {
        return store.get(id);
    }
}