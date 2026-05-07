package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the TellerSession Repository.
 * Used for BDD testing and rapid prototyping.
 */
public class InMemoryTellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerSessionAggregate load(String sessionId) {
        return store.get(sessionId);
    }
}
