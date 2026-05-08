package com.example.mocks;

import com.example.domain.shared.Aggregate;
import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for TellerSessionAggregate.
 * Used exclusively by Cucumber tests and unit tests to avoid DB dependencies.
 */
public class InMemoryTellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
