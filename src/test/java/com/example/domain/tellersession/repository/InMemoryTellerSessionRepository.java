package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TellerSessionRepository for testing.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public void save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSession load(String id) {
        TellerSession a = store.get(id);
        if (a == null) throw new IllegalArgumentException("TellerSession not found: " + id);
        return a;
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}