package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
