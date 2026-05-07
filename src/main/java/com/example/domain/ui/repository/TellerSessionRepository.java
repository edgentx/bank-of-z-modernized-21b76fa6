package com.example.domain.ui.repository;

import com.example.domain.ui.model.TellerSessionAggregate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory repository for TellerSessionAggregate.
 */
public class TellerSessionRepository {
    
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();
    private final Duration defaultTimeout;

    public TellerSessionRepository(Duration defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public TellerSessionAggregate create() {
        String id = UUID.randomUUID().toString();
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id, defaultTimeout);
        store.put(id, aggregate);
        return aggregate;
    }

    public TellerSessionAggregate load(String id) {
        return store.get(id);
    }
}
