package com.example.mocks;

import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock adapter for TellerSession persistence.
 * Not used by the pure Domain unit tests, but required by the prompt's Mock Adapter pattern
 * for potential integration or Cucumber tests.
 */
public class InMemoryTellerSessionRepository {
    
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public TellerSessionAggregate create(String sessionId) {
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        save(aggregate);
        return aggregate;
    }
}