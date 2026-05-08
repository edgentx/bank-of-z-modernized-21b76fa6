package com.example.mocks;

import com.example.domain.shared.Aggregate;
import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerSessionAggregate load(String id) {
        return Optional.ofNullable(store.get(id))
            .orElseThrow(() -> new IllegalArgumentException("TellerSession not found: " + id));
    }
}
