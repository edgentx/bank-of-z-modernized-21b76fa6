package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerAggregate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory repository implementation for TellerSession testing.
 * This avoids DB complexity while satisfying the Hexagonal requirement for ports/adapters.
 */
@Repository
public class TellerSessionRepository {
    private final ConcurrentMap<String, TellerAggregate> store = new ConcurrentHashMap<>();

    public void save(TellerAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerAggregate load(String id) {
        return store.get(id);
    }
}