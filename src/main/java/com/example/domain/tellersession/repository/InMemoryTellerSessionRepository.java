package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate load(String id) {
        TellerSessionAggregate aggregate = store.get(id);
        if (aggregate == null) {
            // Return a fresh aggregate if not found, or throw. Assuming creation-on-load for BDD simplicity.
            return new TellerSessionAggregate(id);
        }
        return aggregate;
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
